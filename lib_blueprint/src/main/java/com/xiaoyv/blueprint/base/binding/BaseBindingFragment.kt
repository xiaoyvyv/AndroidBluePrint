package com.xiaoyv.blueprint.base.binding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.xiaoyv.blueprint.base.BaseFragment
import com.xiaoyv.widget.kts.injectViewBinding

/**
 * BaseBindingFragment
 *
 * @author why
 * @since 2021/10/9
 */
abstract class BaseBindingFragment<BINDING : ViewBinding> : BaseFragment() {
    lateinit var binding: BINDING

    @CallSuper
    override fun createContentView(inflater: LayoutInflater, parent: ViewGroup?): View {
        binding = createContentBinding(layoutInflater, parent)
        return binding.root
    }

    protected open fun createContentBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?
    ): BINDING {
        return injectViewBinding(parent)
    }

    abstract override fun initView()

    abstract override fun initData()
}