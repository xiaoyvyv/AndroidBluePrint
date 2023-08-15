@file:Suppress("DEPRECATION")

package com.xiaoyv.widget.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import com.blankj.utilcode.util.*
import com.xiaoyv.widget.webview.listener.*


/**
 * X5WebView
 *
 * @author why
 * @since 2023/1/7
 */
class UiWebView @JvmOverloads constructor(
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
    internal var x5Interceptors = arrayListOf<UiWebInterceptor>()

    /**
     * WebView 销毁时，遍历回调
     */
    internal val onDestroyListeners = arrayListOf<() -> Unit>()

    /**
     * Title
     */
    internal var titleTextView: AppCompatTextView? = null
    internal var titleBarView: Toolbar? = null
    internal var progressView: UiWebProgressBar? = null

    private var realInnerDownloadListener: DownloadListener? = null

    var invokeHtmlWhenLoadFinish: Boolean = false

    init {
        getResourcesProxy.invoke(resources)

        webViewClient = UiWebViewClient(this)
        webChromeClient = UiWebChromeClient(this)
        initSettings()
    }

    /**
     * 多窗口支持
     */
    var multipleWindows: Boolean = false
        set(value) {
            field = value
            settings.javaScriptCanOpenWindowsAutomatically = value
            settings.setSupportMultipleWindows(value)
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
        settings.allowFileAccessFromFileURLs = true
        // 支持通过JS打开新窗口
        multipleWindows = false
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
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

        // 下载代理
        realInnerDownloadListener = object : UiDownloadListener(this) {
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
        super.setDownloadListener(realInnerDownloadListener)
    }

    override fun setDownloadListener(p0: DownloadListener?) {
        outConfigDownloadListener = p0
    }

    fun addUrlInterceptor(webInterceptor: UiWebInterceptor) {
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

    fun bindWebProgress(progressView: UiWebProgressBar) {
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
        outConfigDownloadListener = null

        stopLoading()
        onDestroyListeners.forEach { it.invoke() }
        clearHistory()
        (parent as? ViewGroup)?.removeView(this)
        super.destroy()
    }

    companion object {
        const val SCHEME_STORAGE = "http://localhost"

        internal var outConfigDownloadListener: DownloadListener? = null

        var getResourcesProxy: (Resources) -> Resources = { it }
    }
}