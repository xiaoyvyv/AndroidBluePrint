package com.xiaoyv.widget.webview

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.DownloadListener
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.xiaoyv.widget.kts.showToastCompat
import com.xiaoyv.widget.webview.helper.WebActionHelper
import com.xiaoyv.widget.webview.helper.WebActionHelper.fetchFileNameWithContentDisposition
import com.xiaoyv.widget.webview.utils.toSafeUri
import java.lang.ref.WeakReference


/**
 * X5DownloadListener
 *
 * @author why
 * @since 2023/1/7
 */
open class UiDownloadListener(private val x5WebView: UiWebView) : DownloadListener {
    override fun onDownloadStart(
        url: String,
        userAgent: String,
        contentDisposition: String,
        mimeType: String,
        contentLength: Long
    ) {
        WebActionHelper.lastAskDialog?.get()?.dismiss()
        WebActionHelper.lastAskDialog?.clear()

        val fileName = contentDisposition.fetchFileNameWithContentDisposition(url, mimeType)
        val fileSize = ConvertUtils.byte2FitMemorySize(contentLength, 2)

        LogUtils.e("下载文件请求：FileName: $fileName, Url: $url")

        val alertDialog = AlertDialog.Builder(x5WebView.context)
            .setMessage("该网页请求下载文件，是否允许？\n\n$fileName\n文件大小：$fileSize")
            .setPositiveButton("允许") { _, _ ->
                startDownload(url, mimeType, fileName)
            }
            .setNegativeButton("取消", null)
            .create()
        alertDialog.show()

        WebActionHelper.lastAskDialog = WeakReference(alertDialog)
    }

    /**
     * 开始下载
     */
    private fun startDownload(url: String, mimeType: String, fileName: String) {
        showToastCompat("开始下载：$fileName")

        val uri = url.toSafeUri()

        val cookie = CookieManager.getInstance().getCookie(url).orEmpty().trim()

        val request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        request.setAllowedOverRoaming(true)
        request.setMimeType(mimeType)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setTitle(fileName)
        request.setDescription("正在下载：$fileName")
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        request.addRequestHeader("Cookie", cookie)
        request.addRequestHeader("Referer", url)

        // 执行
        Utils.getApp().getSystemService(Context.DOWNLOAD_SERVICE)
            .let { it as DownloadManager }
            .enqueue(request)
    }
}
