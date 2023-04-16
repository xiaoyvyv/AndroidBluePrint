package com.xiaoyv.blueprint.fragment

import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.ToastUtils
import com.xiaoyv.blueprint.app.R
import com.xiaoyv.blueprint.app.databinding.FragmentNavBinding
import com.xiaoyv.blueprint.base.mvvm.nav.BaseNavFragment
import com.xiaoyv.blueprint.base.navigate
import com.xiaoyv.floater.widget.callback.setOnFastLimitClickListener

/**
 * TestNavFragment
 *
 * @author why
 * @since 2022/8/28
 */
class TestNavFragment: BaseNavFragment<FragmentNavBinding, TestNavViewModel>() {
    override fun initView() {

    }

    override fun initListener() {
        binding.btTest.setOnFastLimitClickListener {
            navigate(R.id.audioFragment)
        }
    }

    override fun LifecycleOwner.initObserver() {
    }

    override fun onAnimationStart() {
        ToastUtils.showShort("start")
    }

    override fun onAnimationEnd() {
        ToastUtils.showShort("end")
    }
}