@file:Suppress("DEPRECATION")

package com.xiaoyv.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
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
    var onTipDialogListener: OnTipDialogListener? = null

    /**
     * WebView 网络请求拦截器，遍历回调
     */
    internal var x5Interceptors = arrayListOf<X5WebInterceptor>()

    /**
     * WebView 销毁时，遍历回调
     */
    internal val onDestroyListeners = arrayListOf<() -> Unit>()

    /**
     * Title
     */
    internal var titleTextView: AppCompatTextView? = null
    internal var titleBarView: Toolbar? = null
    internal var progressView: X5WebProgress? = null

    internal var realInnerDownloadListener: DownloadListener? = null
    internal var outConfigDownloadListener: DownloadListener? = null

    init {
        getResourcesProxy.invoke(resources)

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

        // 下载代理
        val listener = object : X5DownloadListener(this) {
            override fun onDownloadStart(
                url: String,
                userAgent: String,
                contentDisposition: String,
                mimeType: String,
                contentLength: Long
            ) {
                if (outConfigDownloadListener != null) {
                    outConfigDownloadListener?.onDownloadStart(
                        url,
                        userAgent,
                        contentDisposition,
                        mimeType,
                        contentLength
                    )
                    return
                }
                super.onDownloadStart(url, userAgent, contentDisposition, mimeType, contentLength)
            }
        }
        super.setDownloadListener(listener)
    }

    override fun setDownloadListener(p0: DownloadListener?) {
        outConfigDownloadListener = p0
    }

    fun addUrlInterceptor(webInterceptor: X5WebInterceptor) {
        if (x5Interceptors.contains(webInterceptor)) {
            return
        }
        x5Interceptors.add(webInterceptor)
    }

    fun bindTitleText(textView: AppCompatTextView) {
        this.titleTextView = textView
    }

    fun bindTitleToolbar(toolbar: Toolbar) {
        this.titleBarView = toolbar
    }

    fun bindWebProgress(progressView: X5WebProgress) {
        this.progressView = progressView
    }

    /**
     * 加载Html
     *
     * @param html html
     */
    fun loadHtml(html: String) {
        loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
    }

    /**
     * 加载Html
     *
     * @param baseUrl baseUrl
     * @param html    html
     */
    fun loadHtml(baseUrl: String, html: String) {
        loadDataWithBaseURL(baseUrl, html, "text/html", "utf-8", baseUrl)
    }

    override fun getResources(): Resources {
        return getResourcesProxy.invoke(super.getResources())
    }

    override fun destroy() {
        stopLoading()
        onDestroyListeners.forEach { it.invoke() }
        clearHistory()
        (parent as? ViewGroup)?.removeView(this)
        super.destroy()
    }

    companion object {
        var getResourcesProxy: (Resources) -> Resources = { it }
    }
}