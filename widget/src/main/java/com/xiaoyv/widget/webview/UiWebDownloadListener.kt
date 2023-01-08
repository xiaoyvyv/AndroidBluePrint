package com.xiaoyv.widget.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.DownloadListener
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils

/**
 * 网页下载监听
 *
 * @author 王怀玉
 * @since 2020/11/11
 */
@SuppressLint("InflateParams")
class UiWebDownloadListener(private val context: Context) : DownloadListener {

    override fun onDownloadStart(
        url: String,
        userAgent: String,
        contentDisposition: String,
        mimeType: String,
        contentLength: Long
    ) {
        LogUtils.i(
            "DownloadListener",
            "$url $userAgent $contentDisposition $mimeType $contentLength"
        )
        // 外部浏览器打开下载链接
        ActivityUtils.startActivity(Intent(Intent.ACTION_VIEW).also {
            it.data = Uri.parse(url)
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}