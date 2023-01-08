package com.xiaoyv.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.JavascriptInterface
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.ThreadUtils
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.URLUtil
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.xiaoyv.webview.helper.X5OpenActionHelper

/**
 * X5WebViewClient
 *
 * @author why
 * @since 2023/1/7
 */
@SuppressLint("JavascriptInterface")
open class X5WebViewClient(private val x5WebView: X5WebView) : WebViewClient() {
    private val queryHtmlJavascript = """
        window.cacheArray = [];
        window.cacheArray[0] = document.getElementsByTagName('html')[0].outerText;
        window.cacheArray[1] = document.getElementsByTagName('html')[0].outerHTML;
        window.android_view_client.onPageFinishWithHtml(window.cacheArray[0], window.cacheArray[1]);
    """.trimIndent()

    init {
        x5WebView.addJavascriptInterface(PageFinishJsInterface(), "android_view_client")
    }


    override fun shouldOverrideUrlLoading(webView: WebView, request: WebResourceRequest): Boolean {
        val uri = request.url
        val linkUrl = uri?.toString().orEmpty()
        if (URLUtil.isNetworkUrl(linkUrl)) {
            return super.shouldOverrideUrlLoading(webView, request)
        }

        X5OpenActionHelper.showCanOpenAppDialog(webView, request.url)
        return true
    }

    override fun onReceivedSslError(
        webView: WebView,
        errorHandler: SslErrorHandler,
        error: SslError
    ) {
        AlertDialog.Builder(x5WebView.context)
            .setTitle("温馨提示")
            .setMessage("该网页证书校验错误，是否继续加载网页？")
            .setPositiveButton("继续") { _, _ -> errorHandler.proceed() }
            .setPositiveButton("取消") { _, _ -> errorHandler.cancel() }
            .setCancelable(false)
            .create()
            .apply { setCanceledOnTouchOutside(false) }
            .show()
    }

    override fun onPageStarted(webView: WebView, url: String, favicon: Bitmap?) {
        runOnUiThread {
            x5WebView.onWebLoadListener?.onLoadStart(webView, url, favicon)
        }
    }

    override fun onPageFinished(webView: WebView, url: String) {
        runOnUiThread {
            x5WebView.onWebLoadListener?.onLoadFinish(webView, url)
        }
        webView.evaluateJavascript(queryHtmlJavascript, null)
    }

    inner class PageFinishJsInterface {

        @Suppress("unused")
        @JavascriptInterface
        fun onPageFinishWithHtml(html: String?, text: String?) {
            runOnUiThread {
                x5WebView.onWebLoadListener?.onHtmlLoadListener(
                    x5WebView, x5WebView.url.orEmpty(), html.orEmpty(), text.orEmpty()
                )
            }
        }
    }

    companion object {
        fun runOnUiThread(block: () -> Unit = {}) {
            ThreadUtils.runOnUiThread { block.invoke() }
        }
    }
}