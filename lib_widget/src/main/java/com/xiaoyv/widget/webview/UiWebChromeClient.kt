package com.xiaoyv.widget.webview

import android.net.Uri
import android.os.Message
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import com.xiaoyv.widget.webview.helper.WebActionHelper
import com.xiaoyv.widget.webview.utils.getActivity
import java.lang.ref.WeakReference

/**
 * X5WebChromeClient
 *
 * @author why
 * @since 2023/1/7
 */
open class UiWebChromeClient(private val x5WebView: UiWebView) : WebChromeClient() {
    private var tmpWeb: WeakReference<UiWebView>? = null

    init {
        // WebView 销毁时，tmpWeb 也要清理掉
        x5WebView.onDestroyListeners.add {
            tmpWeb?.get()?.destroy()
            tmpWeb?.clear()
            tmpWeb = null
        }
    }

    override fun onReceivedTitle(webView: WebView, title: String?) {
        val webTitle = title.orEmpty().trim()
            .ifBlank { webView.url.orEmpty() }
            .ifBlank { webView.url.orEmpty() }

        // Title
        x5WebView.titleTextView?.text = webTitle
        x5WebView.titleBarView?.title = webTitle

        x5WebView.onReceivedTitleListener?.onReceivedTitle(webView, title.orEmpty())
    }

    override fun onProgressChanged(webView: WebView, progress: Int) {
        x5WebView.progressView?.setWebProgress(progress)

        x5WebView.onProgressChangeListener?.onProgressChanged(webView, progress)
    }

    override fun onShowFileChooser(
        webView: WebView,
        valueCallback: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        val fileChooseListener = x5WebView.onFileChooseListener
        if (fileChooseListener == null) {
            valueCallback.onReceiveValue(null)
        } else {
            fileChooseListener.onShowFileChooser(
                webView,
                valueCallback,
                fileChooserParams
            )
        }
        return true
    }

    override fun onCreateWindow(
        webView: WebView,
        p1: Boolean,
        p2: Boolean,
        resultMsg: Message
    ): Boolean {
        val transport = resultMsg.obj as WebView.WebViewTransport
        transport.webView = tmpWeb?.get() ?: UiWebView(webView.context).also {
            it.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
                if (UiWebView.outConfigDownloadListener != null) {
                    UiWebView.outConfigDownloadListener?.onDownloadStart(
                        url,
                        userAgent,
                        contentDisposition,
                        mimeType,
                        contentLength
                    )
                } else {
                    UiDownloadListener(it).onDownloadStart(
                        url,
                        userAgent,
                        contentDisposition,
                        mimeType,
                        contentLength
                    )
                }
            }
            it.settings.javaScriptCanOpenWindowsAutomatically = false
            it.settings.setSupportMultipleWindows(false)
            it.webViewClient = object : UiWebViewClient(it) {

                override fun shouldOverrideUrlLoading(
                    webView: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    val linkUrl = request.url.toString()
                    if (URLUtil.isNetworkUrl(linkUrl) || linkUrl.startsWith(UiWebView.SCHEME_STORAGE)) {
                        x5WebView.onWindowListener?.openNewWindow(linkUrl)
                        return true
                    }
                    WebActionHelper.showCanOpenAppDialog(webView, linkUrl)
                    return true
                }
            }
            tmpWeb = WeakReference(it)
        }
        resultMsg.sendToTarget()
        return true
    }

    override fun onCloseWindow(webview: WebView) {
        webview.stopLoading()
        x5WebView.getActivity()?.finish()
    }
}