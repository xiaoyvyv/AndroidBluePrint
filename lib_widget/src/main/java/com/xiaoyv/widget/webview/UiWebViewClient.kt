package com.xiaoyv.widget.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.JavascriptInterface
import android.webkit.MimeTypeMap
import android.webkit.SslErrorHandler
import android.webkit.URLUtil
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ThreadUtils
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.updateWindowParams
import com.xiaoyv.widget.webview.UiWebView.Companion.SCHEME_STORAGE
import com.xiaoyv.widget.webview.helper.WebActionHelper
import java.io.FileInputStream
import java.lang.ref.WeakReference

/**
 * X5WebViewClient
 *
 * @author why
 * @since 2023/1/7
 */
@SuppressLint("JavascriptInterface")
open class UiWebViewClient(private val x5WebView: UiWebView) : WebViewClient() {
    private val queryHtmlJavascript = """
        try {
            window.cacheArray = [];
            window.cacheArray[0] = document.getElementsByTagName('html')[0].outerHTML || '';
            window.cacheArray[1] = document.getElementsByTagName('html')[0].outerText || '';
            window.android.onPageFinishWithHtml(window.cacheArray[0], window.cacheArray[1]);
        } catch(e) {}
    """.trimIndent()

    init {
        x5WebView.addJavascriptInterface(PageFinishJsInterface(), "android")
    }

    override fun shouldOverrideUrlLoading(webView: WebView, request: WebResourceRequest): Boolean {
        val linkUrl = request.url.toString()

        // 多窗口
        if (x5WebView.multipleWindows) {
            x5WebView.onWindowListener?.openNewWindow(linkUrl)
            return true
        }

        if (URLUtil.isNetworkUrl(linkUrl) || linkUrl.startsWith(SCHEME_STORAGE)) {
            x5WebView.loadUrl(linkUrl)
            return true
        }
        WebActionHelper.showCanOpenAppDialog(webView, linkUrl)
        return true
    }

    override fun shouldInterceptRequest(
        webView: WebView,
        request: WebResourceRequest,
    ): WebResourceResponse? {
        for (interceptor in x5WebView.x5Interceptors) {
            val resourceResponse = interceptor.shouldInterceptRequest(webView, request)
            if (resourceResponse != null) {
                return resourceResponse
            }
        }
        val url = request.url.toString()
        if (url.startsWith(SCHEME_STORAGE)) {
            val realPath = url.substringAfter(SCHEME_STORAGE).let {
                if (it.contains("?")) it.substringBefore("?") else it
            }
            if (FileUtils.isFileExists(realPath).not()) {
                return super.shouldInterceptRequest(webView, request)
            }
            val extension = MimeTypeMap.getFileExtensionFromUrl(realPath)
            val mimeType =
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            return WebResourceResponse(mimeType, "utf-8", FileInputStream(realPath))
        }
        return super.shouldInterceptRequest(webView, request)
    }

    override fun onReceivedError(
        webView: WebView,
        request: WebResourceRequest,
        error: WebResourceError?,
    ) {
        super.onReceivedError(webView, request, error)

        runOnUiThread {
            val errorCode = error?.errorCode ?: 0
            val errorMsg = error?.description.toString()
            x5WebView.onWebLoadListener?.onLoadError(errorCode, errorMsg)
        }
    }

    override fun onReceivedHttpError(
        webView: WebView,
        request: WebResourceRequest,
        response: WebResourceResponse?,
    ) {
        runOnUiThread {
            val errorCode = response?.statusCode ?: 0
            val errorMsg = response?.reasonPhrase.orEmpty()
            x5WebView.onWebLoadListener?.onLoadError(errorCode, errorMsg)
        }
    }

    @SuppressLint("WebViewClientOnReceivedSslError")
    override fun onReceivedSslError(
        webView: WebView,
        errorHandler: SslErrorHandler,
        error: SslError,
    ) {
        WebActionHelper.lastAskDialog?.get()?.dismiss()
        WebActionHelper.lastAskDialog?.clear()

        val alertDialog = AlertDialog.Builder(x5WebView.context)
            .setTitle("温馨提示")
            .setMessage("该网页证书校验错误，是否继续加载网页？")
            .setPositiveButton("继续") { _, _ -> errorHandler.proceed() }
            .setNegativeButton("取消") { _, _ -> errorHandler.cancel() }
            .setCancelable(false)
            .create()

        alertDialog.apply { setCanceledOnTouchOutside(false) }
        alertDialog.show()

        alertDialog.window?.updateWindowParams {
            width = ScreenUtils.getScreenWidth() - 160.dpi
        }

        WebActionHelper.lastAskDialog = WeakReference(alertDialog)
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
        if (x5WebView.invokeHtmlWhenLoadFinish) {
            webView.evaluateJavascript(queryHtmlJavascript, null)
        }
    }

    inner class PageFinishJsInterface {

        @Suppress("unused")
        @JavascriptInterface
        fun onPageFinishWithHtml(html: String?, text: String?) {
            runOnUiThread {
                val htmlUrl = x5WebView.url.orEmpty().trim()
                val htmlEncode = html.orEmpty()
                val htmlText = text.orEmpty().replace("\n", "￥")
                    .replace("\t", "￥")
                    .replace(" ", "￥")
                    .replace(" ", "￥")
                    .replace(Regex("￥+"), "￥")

                x5WebView.onWebLoadListener?.onHtmlLoaded(
                    x5WebView, htmlUrl, htmlEncode, htmlText
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