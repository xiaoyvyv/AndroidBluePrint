package com.xiaoyv.blueprint.base.binding

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.xiaoyv.blueprint.base.BaseFragment
import com.xiaoyv.floater.widget.kts.injectViewBinding

/**
 * BaseBindingFragment
 *
 * @author why
 * @since 2021/10/9
 */
abstract class BaseBindingFragment<BINDING : ViewBinding> : BaseFragment() {
    lateinit var binding: BINDING

    @CallSuper
    override fun createContentView(inflater: LayoutInflater, parent: FrameLayout): View {
        binding = createContentBinding(layoutInflater)
        return binding.root
    }

    protected open fun createContentBinding(layoutInflater: LayoutInflater): BINDING {
        return injectViewBinding()
    }

    abstract override fun initView()

    abstract override fun initData()
}