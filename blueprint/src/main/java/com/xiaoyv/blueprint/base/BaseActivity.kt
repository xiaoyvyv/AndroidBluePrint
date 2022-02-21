@file:Suppress("DEPRECATION", "MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import autodispose2.AutoDisposeConverter
import com.blankj.utilcode.util.*
import com.github.nukc.stateview.StateView
import com.gyf.immersionbar.ImmersionBar
import com.xiaoyv.blueprint.BluePrint
import com.xiaoyv.blueprint.R
import com.xiaoyv.widget.databinding.UiViewStateEmptyBinding
import com.xiaoyv.widget.databinding.UiViewStateRetryBinding
import com.xiaoyv.widget.dialog.loading.UiLoadingDialog
import io.reactivex.rxjava3.core.ObservableTransformer
import me.jessyan.autosize.AutoSizeCompat
import me.jessyan.autosize.internal.CancelAdapt


/**
 * BaseActivity
 *
 * @author why
 * @since 2020/11/28
 */
abstract class BaseActivity : AppCompatActivity(), IBaseView {
    private lateinit var rootView: FrameLayout
    private lateinit var loading: UiLoadingDialog

    /**
     * 状态布局
     */
    private var csvStatusView: StateView? = null
    val requireStateView: StateView
        get() = vGetStateView()

    /**
     * 是否重复执行动画
     */
    private var repeatAnimation = false

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
        initListener()
    }

    @CallSuper
    override fun getResources(): Resources? {
        if (this is CancelAdapt) {
            return super.getResources()
        }
        // 解决 AutoSize 横屏时对话框显示状态，切后台再切回前台导致的适配失效问题
        val resources = super.getResources()
        runOnUiThread {
            AutoSizeCompat.autoConvertDensity(
                resources,
                BluePrint.MAX_WIDTH_DP,
                !ScreenUtils.isLandscape()
            )
        }
        return super.getResources()
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
            .autoDarkModeEnable(true)
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

    private fun initBaseView() {
        rootView = findViewById(android.R.id.content)
        loading = UiLoadingDialog()
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

    /**
     * 页面动画效果
     *
     * 返回 true，下次还会继续执行该方法播放动画
     */
    protected open fun initAnimation() = false

    override fun p2vShowToast(msg: String?) {

    }

    override fun p2vShowSnack(msg: String?, snackBarType: Int) {

    }

    override fun p2vShowLoading(msg: String?) {
        if (isFinishing || isDestroyed) {
            return
        }
        loading.dismiss()
        loading.showWithMsg(supportFragmentManager, msg)
    }

    override fun p2vHideLoading() {
        if (isFinishing || isDestroyed) {
            return
        }
        loading.dismiss()
    }

    override fun p2vShowNormalView() {
        requireStateView.showContent()
    }

    override fun p2vShowLoadingView() {
        requireStateView.showLoading()
    }

    override fun p2vShowEmptyView() {
        requireStateView.showEmpty()
    }

    override fun p2vShowTipView(msg: String?) {
        requireStateView.showEmpty().also {
            val stateBinding = UiViewStateEmptyBinding.bind(it)
            stateBinding.tvStatus.text =
                msg ?: StringUtils.getString(R.string.ui_view_status_empty)
        }
    }

    override fun p2vShowRetryView() {
        p2vShowRetryView(null, null)
    }

    override fun p2vShowRetryView(msg: String?) {
        p2vShowRetryView(msg, null)
    }

    override fun p2vShowRetryView(msg: String?, btText: String?) {
        requireStateView.showRetry().also {
            val stateBinding = UiViewStateRetryBinding.bind(it)
            stateBinding.tvStatus.text =
                msg ?: StringUtils.getString(R.string.ui_view_status_retry)
            stateBinding.btRefresh.text =
                btText ?: StringUtils.getString(R.string.ui_view_status_refresh)
        }
    }

    override fun vGetStateView() = csvStatusView ?: StateView(this).also {
        it.emptyResource = R.layout.ui_view_state_empty
        it.loadingResource = R.layout.ui_view_state_loading
        it.retryResource = R.layout.ui_view_state_retry
        it.onRetryClickListener = object : StateView.OnRetryClickListener {
            override fun onRetryClick() {
                vRetryClick()
            }
        }

        csvStatusView = it
        rootView.addView(
            it,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ).apply {
                topMargin = stateViewTopMargin()
            }
        )
    }

    override fun vRetryClick() {
        LogUtils.i("vRetryClick")
    }

    /**
     * 状态布局顶部缩进
     */
    protected open fun stateViewTopMargin(): Int = 0

    /**
     * 统一线程处理
     */
    protected fun <T : Any> bindTransformer(): ObservableTransformer<T, T> {
        return BluePrint.bindTransformer()
    }

    /**
     * 绑定生命周期
     */
    protected fun <T : Any> bindLifecycle(): AutoDisposeConverter<T> {
        return BluePrint.bindLifecycle(this)
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
        loading.dismiss()
        super.onDestroy()
    }
}