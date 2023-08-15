package com.xiaoyv.widget.webview.listener

import android.webkit.WebView


/**
 * 网页进度改变监听
 *
 * @author 王怀玉
 * @since 2020/11/11
 */
interface OnProgressChangeListener {

    fun onProgressChanged(webView: WebView, progress: Int)
}