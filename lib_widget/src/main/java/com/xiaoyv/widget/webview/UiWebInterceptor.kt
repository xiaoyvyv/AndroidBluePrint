package com.xiaoyv.widget.webview

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView


/**
 * X5WebInterceptor
 *
 * @author why
 * @since 2022/9/25
 */
interface UiWebInterceptor {

    fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
        return null
    }
}