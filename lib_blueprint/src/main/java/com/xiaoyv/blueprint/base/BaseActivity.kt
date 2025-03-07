@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.KeyboardUtils
import com.github.nukc.stateview.StateView
import com.jeremyliao.liveeventbus.LiveEventBus
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.blueprint.localize.LocalizeManager.attachBaseContextWithLanguage
import com.xiaoyv.widget.adapt.autoConvertDensity
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.dialog.UiLoadingDialog
import com.xiaoyv.widget.kts.showToastCompat
import com.xiaoyv.widget.kts.useNotNull
import com.xiaoyv.widget.stateview.EmptyStateController
import com.xiaoyv.widget.stateview.IStateController


/**
 * BaseActivity
 *
 * @author why
 * @since 2020/11/28
 */
abstract class BaseActivity : AppCompatActivity(), IBaseView {
    private lateinit var rootContent: FrameLayout

    /**
     * Loading 相关控制
     */
    protected lateinit var loadingDialog: UiDialog
    protected lateinit var loadingStateView: IStateController

    override val stateController: IStateController
        get() = loadingStateView

    /**
     * 是否重复执行动画
     */
    private var repeatAnimation = false

    /**
     * 是否第一次获取焦点
     */
    private var firstFocus = true

    val requireActivity: BaseActivity
        get() = this@BaseActivity

    /**
     * 获取当前是否开启深色模式
     */
    val nightMode: Boolean
        get() = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 栏相关
        initBarConfig()

        // 窗口配置
        useNotNull(window) {
            initWindowConfig(this)
        }

        // 传递参数
        useNotNull(intent) {
            initIntentData(this, extras ?: Bundle.EMPTY, false)
        }

        // 设置视图
        useNotNull(createContentView()) {
            setContentView(this)
        }

        // 解决全屏 SoftInputModel 失效问题
        if (fix5497()) {
            KeyboardUtils.fixAndroidBug5497(this)
        }
        KeyboardUtils.fixSoftInputLeaks(this)

        // 初始化相关回调
        initBaseView()
        initView()
        initFun()
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet,
    ): View? {
        resources.autoConvertDensity()
        return super.onCreateView(parent, name, context, attrs)
    }

    protected open fun initFun() {
        initData()
        initEvent()
        initObserver()
        initViewObserver()
        initListener()
    }

    @CallSuper
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.also {
            initIntentData(it, it.extras ?: Bundle.EMPTY, true)
        }
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        if (!repeatAnimation) {
            repeatAnimation = initAnimation()
        }
    }

    protected open fun fix5497(): Boolean = false

    protected open fun initBarConfig() {
        BaseConfig.config.globalConfig.initBarConfig(this, nightMode)
    }

    protected open fun initWindowConfig(window: Window) {
        BaseConfig.config.globalConfig.initWindowConfig(this, window)
    }

    protected open fun initBaseView() {
        rootContent = findViewById(android.R.id.content)

        // Loading
        loadingDialog = onCreateLoadingDialog()
        loadingStateView = onCreateStateController()
    }

    /**
     * 初始化V
     *
     * @return 初始化V
     */
    protected abstract fun createContentView(): View?

    protected open fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {}

    /**
     * 初始化视图
     */
    protected abstract fun initView()

    /**
     * 初始化数据
     */
    protected abstract fun initData()
    protected open fun initEvent() {}
    protected open fun initListener() {}
    protected open fun initObserver() {}
    protected open fun LifecycleOwner.initViewObserver() {}

    /**
     * 页面动画效果
     *
     * 返回 true，下次还会继续执行该方法播放动画
     */
    protected open fun initAnimation() = false

    /**
     * 创建 LoadingDialog
     */
    protected open fun onCreateLoadingDialog(): UiDialog {
        return UiLoadingDialog()
    }

    /**
     * 创建 IStateController
     */
    override fun onCreateStateController(): IStateController {
        return EmptyStateController()
    }

    /**
     * 添加 RxEvent TAG 接收
     */
    fun <T> addReceiveEventTag(key: String, type: Class<T>, observer: Observer<T>) {
        LiveEventBus.get(key, type).observe(this, observer)
    }

    @CallSuper
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus && firstFocus) {
            firstFocus = false
            onWindowFirstFocus()
        }
    }

    /**
     * Window 第一次获取焦点
     */
    open fun onWindowFirstFocus() {

    }

    override fun showSnack(msg: String?, snackBarType: Int) {

    }

    override fun showToast(msg: String?) {
        showToastCompat(msg.orEmpty())
    }

    override fun showLoading(msg: String?) {
        if (isFinishing || isDestroyed) return

        loadingDialog.message = msg
        loadingDialog.showLoading(activity, msg)
    }

    override fun hideLoading() {
        if (isFinishing || isDestroyed) return
        loadingDialog.dismissLoading()
    }

    /**
     * 重试或刷新点击
     */
    override fun onClickStateView(stateView: StateView, view: View) {

    }

    @CallSuper
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.attachBaseContextWithLanguage())
    }

    @CallSuper
    override fun getResources(): Resources {
        return super.getResources().autoConvertDensity()
    }

    @CallSuper
    override fun onDestroy() {
        loadingDialog.dismissLoading()
        super.onDestroy()
    }
}