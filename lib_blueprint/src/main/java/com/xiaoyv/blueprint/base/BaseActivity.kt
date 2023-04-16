@file:Suppress("DEPRECATION", "MemberVisibilityCanBePrivate", "OVERRIDE_DEPRECATION")

package com.xiaoyv.blueprint.base

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.FragmentUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.nukc.stateview.StateView
import com.gyf.immersionbar.ImmersionBar
import com.jeremyliao.liveeventbus.LiveEventBus
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.blueprint.localize.LocalizeManager.attachBaseContextWithLanguage
import com.xiaoyv.floater.widget.adapt.autoConvertDensity
import com.xiaoyv.floater.widget.dialog.UiDialog
import com.xiaoyv.floater.widget.dialog.UiLoadingDialog
import com.xiaoyv.floater.widget.kts.useNotNull
import com.xiaoyv.floater.widget.stateview.EmptyStateController
import com.xiaoyv.floater.widget.stateview.IStateController


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

    protected open fun initFun() {
        initData()
        initEvent()
        initObserver()
        initViewObserver()
        initListener()
    }

    @CallSuper
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.also {
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
        ImmersionBar.with(this)
            .transparentStatusBar()
            .statusBarDarkFont(true)
            .init()
    }

    protected open fun initWindowConfig(window: Window) {
        // 竖屏
        ScreenUtils.setPortrait(this)

        // 窗口参数
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
    }

    protected open fun initBaseView() {
        rootContent = findViewById(android.R.id.content)

        // Loading
        loadingDialog = createLoadingDialog()
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
    protected open fun createLoadingDialog(): UiDialog {
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
        ToastUtils.showShort(msg.orEmpty())
    }

    override fun showLoading(msg: String?) {
        if (isFinishing || isDestroyed) return

        loadingDialog.message = msg
        loadingDialog.show(activity, msg)
    }

    override fun hideLoading() {
        if (isFinishing || isDestroyed) return
        loadingDialog.dismiss()
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
    override fun onBackPressed() {
        FragmentUtils.getFragments(supportFragmentManager).forEach {
            // 判断事件是否被消费掉了
            if (it is BaseFragment && it.onFragmentBackPressed()) {
                return
            }
        }
        super.onBackPressed()
    }

    @CallSuper
    override fun onDestroy() {
        loadingDialog.dismiss()
        super.onDestroy()
    }
}