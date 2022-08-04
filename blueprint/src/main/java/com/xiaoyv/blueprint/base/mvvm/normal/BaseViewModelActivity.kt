@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base.mvvm.normal

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.xiaoyv.blueprint.base.BaseActivity
import com.xiaoyv.blueprint.base.createBinding
import com.xiaoyv.blueprint.base.createViewModel

/**
 * BaseModelActivity
 *
 * @author why
 * @since 2022/8/3
 **/
abstract class BaseViewModelActivity<VB : ViewBinding, VM : BaseViewModel> : BaseActivity() {
    protected lateinit var binding: VB

    protected val viewModel: VM by createViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
        viewModel.onAttach(this)
        viewModel.onViewCreated()
    }

    @CallSuper
    override fun createContentView(): View {
        binding = createBinding()
        return binding.root
    }

    abstract override fun initView()

    abstract override fun initData()

    protected open fun initObserver() {}

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDetach()
        viewModel.onViewDestroy()
        viewModel.onDestroy()
    }
}