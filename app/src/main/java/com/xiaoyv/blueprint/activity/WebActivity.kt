package com.xiaoyv.blueprint.activity

import android.view.LayoutInflater
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.xiaoyv.blueprint.app.databinding.ActivityWebBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.webview.listener.OnWindowListener

/**
 * WebActivity
 *
 * @author why
 * @since 2022/5/10
 */
class WebActivity : BaseBindingActivity<ActivityWebBinding>() {
    //        private var webUrl: String = "https://web.xiaoyv.com.cn"
//    private var webUrl: String = "https://atrust.yangtzeu.edu.cn:4443"
    private var webUrl: String = "https://bilibili.com"


    override fun createContentBinding(layoutInflater: LayoutInflater): ActivityWebBinding {
        return ActivityWebBinding.inflate(layoutInflater)
    }

    override fun initView() {
        webUrl = intent.getStringExtra("webUrl").orEmpty().ifBlank { webUrl }

        onBackPressedDispatcher.addCallback {

            if (binding.web.canGoBack()) {
                ToastUtils.showShort("back")
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
        binding.web.loadUrl(webUrl)

        ThreadUtils.runOnUiThreadDelayed({
            binding.web.loadUrl("https://beian.miit.gov.cn/")
        }, 3000)
    }

    override fun onDestroy() {
        binding.web.destroy()
        super.onDestroy()
    }
}