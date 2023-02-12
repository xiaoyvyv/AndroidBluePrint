package com.xiaoyv.webview

import android.net.Uri
import android.os.Message
import com.blankj.utilcode.util.LogUtils
import com.tencent.smtt.sdk.URLUtil
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import com.xiaoyv.webview.helper.X5ActionHelper
import com.xiaoyv.webview.utils.getActivity
import java.lang.ref.WeakReference

/**
 * X5WebChromeClient
 *
 * @author why
 * @since 2023/1/7
 */
class X5WebChromeClient(private val x5WebView: X5WebView) : WebChromeClient() {
    private var tmpWeb: WeakReference<X5WebView>? = null

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
        transport.webView = tmpWeb?.get() ?: X5WebView(webView.context).also {
            it.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
                if (X5WebView.outConfigDownloadListener != null) {
                    X5WebView.outConfigDownloadListener?.onDownloadStart(
                        url,
                        userAgent,
                        contentDisposition,
                        mimeType,
                        contentLength
                    )
                } else {
                    X5DownloadListener(it).onDownloadStart(
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
            it.webViewClient = object : X5WebViewClient(it) {
                override fun shouldOverrideUrlLoading(webView: WebView, linkUrl: String): Boolean {
                    LogUtils.i("new window => $linkUrl")

                    if (URLUtil.isNetworkUrl(linkUrl)) {
                        x5WebView.onWindowListener?.openNewWindow(linkUrl)
                        return true
                    }
                    X5ActionHelper.showCanOpenAppDialog(webView, linkUrl)
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