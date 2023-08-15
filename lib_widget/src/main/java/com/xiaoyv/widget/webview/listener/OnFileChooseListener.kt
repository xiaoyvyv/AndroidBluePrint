package com.xiaoyv.widget.webview.listener

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

/**
 * 网页文件选择
 *
 * @author 王怀玉
 * @since 2020/11/11
 */
interface OnFileChooseListener {

    fun onShowFileChooser(
        webView: WebView,
        valueCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    )
}