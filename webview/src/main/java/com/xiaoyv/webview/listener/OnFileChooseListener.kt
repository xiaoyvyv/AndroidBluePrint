package com.xiaoyv.webview.listener

import android.net.Uri
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView

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