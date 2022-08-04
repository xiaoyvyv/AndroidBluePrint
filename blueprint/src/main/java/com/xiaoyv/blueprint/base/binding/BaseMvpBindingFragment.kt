package com.xiaoyv.blueprint.base.binding

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.xiaoyv.blueprint.base.BaseMvpFragment
import com.xiaoyv.blueprint.base.IBaseView
import com.xiaoyv.blueprint.base.ImplBasePresenter

/**
 * BaseMvpBindingFragment
 *
 * @author why
 * @since 2021/10/9
 */
abstract class BaseMvpBindingFragment<BINDING : ViewBinding, V : IBaseView, PRESENTER : ImplBasePresenter<V>> :
    BaseMvpFragment<V, PRESENTER>() {

    lateinit var binding: BINDING

    @CallSuper
    override fun createContentView(inflater: LayoutInflater, flRoot: FrameLayout): View {
        binding = createContentBinding(layoutInflater)
        return binding.root
    }

    abstract override fun createPresenter(): PRESENTER

    abstract fun createContentBinding(layoutInflater: LayoutInflater): BINDING

    abstract override fun initView()

    abstract override fun initData()

}