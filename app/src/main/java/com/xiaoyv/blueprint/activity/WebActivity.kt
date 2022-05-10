package com.xiaoyv.blueprint.activity

import android.view.LayoutInflater
import com.xiaoyv.blueprint.app.databinding.ActivityWebBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity

/**
 * WebActivity
 *
 * @author why
 * @since 2022/5/10
 */
class WebActivity : BaseBindingActivity<ActivityWebBinding>() {

    override fun createContentBinding(layoutInflater: LayoutInflater): ActivityWebBinding {
        return ActivityWebBinding.inflate(layoutInflater)
    }

    override fun initView() {

    }

    override fun initData() {
        binding.web.loadUrl("https://www.baidu.com")
    }

    override fun onDestroy() {
        binding.web.destroy()
        super.onDestroy()
    }
}