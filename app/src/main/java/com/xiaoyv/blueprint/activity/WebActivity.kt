package com.xiaoyv.blueprint.activity

import android.view.LayoutInflater
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.xiaoyv.blueprint.app.databinding.ActivityWebBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.widget.kts.showToastCompat
import com.xiaoyv.webview.listener.OnWindowListener

/**
 * WebActivity
 *
 * @author why
 * @since 2022/5/10
 */
class WebActivity : BaseBindingActivity<ActivityWebBinding>() {
    private var webUrl: String = "https://bilibili.com"

    override fun createContentBinding(layoutInflater: LayoutInflater): ActivityWebBinding {

        return ActivityWebBinding.inflate(layoutInflater)
    }

    override fun initView() {
        webUrl = intent.getStringExtra("webUrl").orEmpty().ifBlank { webUrl }

        onBackPressedDispatcher.addCallback {

            if (binding.web.canGoBack()) {
                showToastCompat("back")
                binding.web.goBack()
                return@addCallback
            }

            isEnabled = false
            onBackPressedDispatcher.onBackPressed()
            isEnabled = true
        }

        binding.web.onWindowListener = object : OnWindowListener {
            override fun openNewWindow(url: String) {
                LogUtils.e(" open window ==>")

                ActivityUtils.startActivity(
                    bundleOf("webUrl" to url), WebActivity::class.java
                )
            }
        }
    }

    override fun initData() {
        val userAgent = binding.web.settings.userAgentString.replace("; wv", "")
        binding.web.settings.userAgentString = userAgent
        binding.web.loadUrl(webUrl)
    }

    override fun onDestroy() {
        binding.web.destroy()
        super.onDestroy()
    }
}