package com.xiaoyv.webview

import com.blankj.utilcode.util.ToastUtils
import com.tencent.smtt.export.external.extension.proxy.ProxyWebChromeClientExtension

/**
 * X5WebChromeClientExtension
 *
 * @author why
 * @since 2023/1/7
 */
open class X5WebChromeClientExtension(x5WebView: X5WebView) : ProxyWebChromeClientExtension() {
    override fun onPrintPage() {
        ToastUtils.showShort("打印")
    }


}