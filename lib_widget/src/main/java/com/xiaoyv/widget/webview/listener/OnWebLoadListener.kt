package com.xiaoyv.widget.webview.listener

import android.graphics.Bitmap
import android.webkit.WebView

/**
 * 网页加载完成监听
 *
 * @author 王怀玉
 * @since 2020/11/11
 */
interface OnWebLoadListener {
    /**
     * 加载开始回调
     *
     * @param webView 网页
     * @param url url
     */
    fun onLoadStart(webView: WebView, url: String, favicon: Bitmap?) {}

    /**
     * 加载完成回调
     *
     * @param webView 网页
     * @param url url
     */
    fun onLoadFinish(webView: WebView, url: String) {}

    /**
     * Html 监听
     *
     * @param webView 网页
     * @param url url
     */
    fun onHtmlLoaded(webView: WebView, url: String, html: String, text: String) {}

    /**
     * 加载失败
     *
     * @param error error
     */
    fun onLoadError(code: Int, error: String) {}
}