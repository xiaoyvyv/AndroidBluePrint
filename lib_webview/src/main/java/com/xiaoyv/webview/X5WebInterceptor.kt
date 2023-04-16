package com.xiaoyv.webview

import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.WebView

/**
 * X5WebInterceptor
 *
 * @author why
 * @since 2022/9/25
 */
interface X5WebInterceptor {

    fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
        return null
    }
}