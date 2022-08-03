package com.xiaoyv.blueprint.base.mvvm.normal

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
class BaseViewModelActivity<VB : ViewBinding, VM : BaseViewModel> : BaseActivity() {
    lateinit var binding: VB

    val viewModel: VM by createViewModel()

    @CallSuper
    override fun createContentView(): View {
        binding = createBinding()
        return binding.root
    }

    override fun initView() {

    }

    override fun initData() {

    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDetach()
        viewModel.onDestroy()
    }
}