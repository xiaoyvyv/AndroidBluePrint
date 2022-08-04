package com.xiaoyv.blueprint.activity

import android.view.ViewGroup
import com.blankj.utilcode.util.LogUtils
import com.xiaoyv.blueprint.app.databinding.ActivityMvvmBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.utils.parentView

/**
 * MvvmTestActivity
 *
 * @author why
 * @since 2022/8/4
 **/
class MvvmTestActivity : BaseViewModelActivity<ActivityMvvmBinding, MvvmTestViewModel>() {

    override fun initView() {
        LogUtils.e("initView")
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.tvHello.setOnFastLimitClickListener {
            viewModel.changeTip()
            binding.root.parentView<ViewGroup>()
        }
    }

    override fun initObserver() {
        viewModel.helloObserver.observe(this) {
            binding.tvHello.text = it
        }
    }
}