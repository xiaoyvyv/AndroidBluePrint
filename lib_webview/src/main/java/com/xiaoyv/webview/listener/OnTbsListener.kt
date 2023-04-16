package com.xiaoyv.webview.listener

import com.tencent.smtt.sdk.TbsListener

/**
 * OnTbsListener
 *
 * @author why
 * @since 2023/1/8
 */
interface OnTbsListener : TbsListener {
    override fun onDownloadFinish(p0: Int) {}

    override fun onInstallFinish(status: Int) {}

    override fun onDownloadProgress(progress: Int) {}
}

