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
import com.xiaoyv.blueprint.localize.LocalizeManager.attachBaseContextWithLanguage
import com.xiaoyv.widget.adapt.autoConvertDensity
import com.xiaoyv.widget.dialog.UiLoadingDialog
import com.xiaoyv.widget.stateview.StateViewImpl
import java.lang.ref.WeakReference


/**
 * BaseActivity
 *
 * @author why
 * @since 2020/11/28
 */
abstract class BaseActivity : AppCompatActivity(), IBaseView {
    private lateinit var rootContent: FrameLayout

    private var loadingDialog: UiLoadingDialog? = null

    private var reference: WeakReference<StateView>? = null
    private var stateViewImpl: StateViewImpl? = null

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
        window?.also {
            initWindowConfig(it)
        }

        // 传递参数
        intent?.also {
            it.extras?.apply {
                initIntentData(it, this, false)
            }
        }

        // 设置视图
        createContentView()?.also {
            setContentView(it)
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
        initViewObserver()
        initListener()
    }

    @CallSuper
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.also {
            initIntentData(it, it.extras ?: return, true)
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
                    or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        )
    }

    protected open fun initBaseView() {
        rootContent = findViewById(android.R.id.content)

        loadingDialog = UiLoadingDialog()
        loadingDialog?.isCancelable = false


        // 状态布局
        stateViewImpl = object : StateViewImpl(requireActivity) {
            override fun onCreateStateView(): StateView {
                var stateView = reference?.get()
                if (stateView != null) {
                    return stateView
                }

                stateView = createStateView(
                    requireActivity,
                    rootContent,
                    this@BaseActivity::p2vClickStatusView
                )
                reference = WeakReference(stateView)
                return stateView
            }
        }
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

    protected open fun LifecycleOwner.initViewObserver() {}

    /**
     * 页面动画效果
     *
     * 返回 true，下次还会继续执行该方法播放动画
     */
    protected open fun initAnimation() = false

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

    override fun p2vShowSnack(msg: String?, snackBarType: Int) {

    }

    override fun p2vShowToast(msg: String?) {
        ToastUtils.showShort(msg.orEmpty())
    }

    override fun p2vShowLoading(msg: String?) {
        if (isFinishing || isDestroyed) {
            return
        }

        loadingDialog?.message = msg
        loadingDialog?.show(this, msg)
    }

    override fun p2vHideLoading() {
        if (isFinishing || isDestroyed) {
            return
        }
        loadingDialog?.dismiss()
    }

    override fun p2vGetStateController(): StateViewImpl {
        return stateViewImpl ?: throw NullPointerException("stateViewImpl is null !!!")
    }

    /**
     * 重试或刷新点击
     */
    override fun p2vClickStatusView(stateView: StateView, view: View) {

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
        loadingDialog?.dismiss()
        loadingDialog = null
        super.onDestroy()
    }
}