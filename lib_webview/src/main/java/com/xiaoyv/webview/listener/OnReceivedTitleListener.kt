package com.xiaoyv.webview.listener

import com.tencent.smtt.sdk.WebView

/**
 * OnReceivedTitleListener
 *
 * @author why
 * @since 2023/1/7
 */
interface OnReceivedTitleListener {
    fun onReceivedTitle(webView: WebView, title: String)
}