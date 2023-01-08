@file:Suppress("DEPRECATION")

package com.xiaoyv.webview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.blankj.utilcode.util.*
import com.tencent.smtt.sdk.*
import com.xiaoyv.webview.listener.*


/**
 * X5WebView
 *
 * @author why
 * @since 2023/1/7
 */
class X5WebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : WebView(context, attrs) {

    /**
     * Listener
     */
    var onWindowListener: OnWindowListener? = null
    var onFileChooseListener: OnFileChooseListener? = null
    var onProgressChangeListener: OnProgressChangeListener? = null
    var onWebLoadListener: OnWebLoadListener? = null
    var onReceivedTitleListener: OnReceivedTitleListener? = null

    /**
     * WebView 销毁时，遍历回调
     */
    internal val onDestroyListeners = arrayListOf<() -> Unit>()

    init {
        webViewClient = X5WebViewClient(this)
        webViewClientExtension = X5WebViewClientExtension(this)
        webChromeClient = X5WebChromeClient(this)
        webChromeClientExtension = X5WebChromeClientExtension(this)
        initSettings()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initSettings() {
        settings.javaScriptEnabled = true
        // 设置屏幕适应，将图片调整到适合WebView的大小
        settings.useWideViewPort = true
        // 设置屏幕适应，缩放至屏幕的大小
        settings.loadWithOverviewMode = true
        // 缩放操作
        settings.setSupportZoom(true)
        // 设置使用内置的缩放控件
        settings.builtInZoomControls = true
        // 隐藏原生的缩放控件
        settings.displayZoomControls = false

        // 其他细节操作
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.setAllowFileAccessFromFileURLs(true)
        // 支持通过JS打开新窗口
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.setSupportMultipleWindows(true)
        // 支持自动加载图片
        settings.loadsImagesAutomatically = true
        // 设置编码格式
        settings.defaultTextEncodingName = "UTF-8"
        settings.setGeolocationEnabled(true)
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.databaseEnabled = true
        settings.domStorageEnabled = true
        settings.blockNetworkImage = false
        // 取消系统字体缩放
        settings.textZoom = 100

        settings.databasePath = PathUtils.getInternalAppFilesPath() + "/databases"

        // 设置混合协议
        settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

        // 下载
        setDownloadListener(X5DownloadListener(this))
    }

    override fun destroy() {
        stopLoading()
        onDestroyListeners.forEach { it.invoke() }
        clearHistory()
        (parent as? ViewGroup)?.removeView(this)
        super.destroy()
    }
}