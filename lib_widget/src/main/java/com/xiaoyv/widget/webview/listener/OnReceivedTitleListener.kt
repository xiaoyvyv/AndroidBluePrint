package com.xiaoyv.widget.webview.listener

import android.webkit.WebView

/**
 * OnReceivedTitleListener
 *
 * @author why
 * @since 2023/1/7
 */
interface OnReceivedTitleListener {
    fun onReceivedTitle(webView: WebView, title: String)
}