package com.xiaoyv.blueprint.base.binding

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.xiaoyv.blueprint.base.BaseMvpActivity
import com.xiaoyv.blueprint.base.IBaseView
import com.xiaoyv.blueprint.base.ImplBasePresenter
import com.xiaoyv.widget.kts.injectViewBinding

/**
 * BaseMvpBindingActivity
 *
 * @author why
 * @since 2021/10/9
 */
abstract class BaseMvpBindingActivity<BINDING : ViewBinding, V : IBaseView, PRESENTER : ImplBasePresenter<V>> :
    BaseMvpActivity<V, PRESENTER>() {

    lateinit var binding: BINDING

    @CallSuper
    override fun createContentView(): View {
        binding = createContentBinding(layoutInflater)
        return binding.root
    }

    abstract override fun createPresenter(): PRESENTER

    protected open fun createContentBinding(layoutInflater: LayoutInflater): BINDING {
        return injectViewBinding()
    }

    abstract override fun initView()

    abstract override fun initData()

}