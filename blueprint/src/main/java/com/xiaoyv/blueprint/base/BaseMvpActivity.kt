package com.xiaoyv.blueprint.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.github.nukc.stateview.StateView
import com.xiaoyv.blueprint.BluePrint
import java.lang.RuntimeException

/**
 * Mvp Activity基类
 *
 * @author why
 * @since 2020/10/16
 */
abstract class BaseMvpActivity<V : IBaseView, PRESENTER : ImplBasePresenter<V>> : BaseActivity(),
    IBaseView {

    open lateinit var mActivity: AppCompatActivity
    open lateinit var presenter: PRESENTER

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkV()

        mActivity = this@BaseMvpActivity

        presenter = createPresenter()
        presenter.attachView(mActivity as V, this@BaseMvpActivity)

        // 生命周期监听
        initLifecycleObserver(lifecycle)
        // 执行 BaseActivity 初始化事件
        super.initFun()
        // P层创建完成
        onPresenterCreated()
    }

    /**
     * 验证 MVP 是否实现了 V 层接口
     */
    private fun checkV() {
        val check = BluePrint.checkV(this::class.java)
        if (!check) {
            throw RuntimeException("${javaClass.name} 未实现 MVP 的 V 层接口")
        }
    }


    override fun initFun() {
        // 拦截 BaseActivity 初始化事件
        // 避免在 super.onCreate() 时就被调用
        // 应该在 presenter 创建完再调用数据初始化
    }

    /**
     * P 层
     *
     * @return PRESENTER
     */
    protected abstract fun createPresenter(): PRESENTER

    abstract override fun createContentView(): View?

    abstract override fun initView()

    abstract override fun initData()

    /**
     * P层初始化完成，在这使用Presenter进行数据请求
     */
    protected open fun onPresenterCreated() {}

    @CallSuper
    @MainThread
    protected fun initLifecycleObserver(lifecycle: Lifecycle) {
        presenter.setLifecycleOwner(this@BaseMvpActivity)
        lifecycle.addObserver(presenter)
    }

    override fun p2vClickStatusView(stateView: StateView, view: View) {
        onPresenterCreated()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}