package com.xiaoyv.blueprint.base.binding

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.xiaoyv.blueprint.base.BaseFragment

/**
 * BaseBindingFragment
 *
 * @author why
 * @since 2021/10/9
 */
abstract class BaseBindingFragment<BINDING : ViewBinding> : BaseFragment() {
    lateinit var binding: BINDING

    @CallSuper
    override fun createContentView(inflater: LayoutInflater, flRoot: FrameLayout): View {
        binding = createContentBinding(layoutInflater)
        return binding.root
    }

    abstract fun createContentBinding(layoutInflater: LayoutInflater): BINDING

    abstract override fun initView()

    abstract override fun initData()
}