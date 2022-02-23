package com.xiaoyv.blueprint.base

import android.os.Bundle
import android.view.View
import com.github.nukc.stateview.StateView
import com.xiaoyv.blueprint.BluePrint

/**
 * Mvp Fragment 基类
 *
 * @author why
 * @since  2020/10/20
 */
abstract class BaseMvpFragment<V : IBaseView, T : ImplBasePresenter<V>> : BaseFragment(),
    IBaseView {

    open lateinit var presenter: T

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkV()

        presenter = createPresenter()
        presenter.attachView(this@BaseMvpFragment as V, hostActivity)

        if (bindHostActivityLifecycle()) {
            presenter.setLifecycleOwner(hostActivity)
        } else {
            presenter.setLifecycleOwner(this@BaseMvpFragment)
        }
        lifecycle.addObserver(presenter)
    }

    /**
     * 验证是否实现了 V 层接口
     */
    private fun checkV() {
        val check = BluePrint.checkV(this::class.java)
        if (!check) {
            throw RuntimeException("${javaClass.name} 未实现 MVP 的 V 层接口")
        }
    }

    protected abstract fun createPresenter(): T

    abstract override fun createContentView(): View?

    abstract override fun initView()

    abstract override fun initData()

    override fun p2vClickStatusView(stateView: StateView, view: View) {
        initFinish()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    open fun bindHostActivityLifecycle() = true
}