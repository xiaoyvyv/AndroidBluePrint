package com.xiaoyv.webview

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.DownloadListener
import com.tencent.smtt.sdk.MimeTypeMap
import com.xiaoyv.webview.helper.X5OpenActionHelper
import com.xiaoyv.webview.utils.toSafeUri
import java.lang.ref.WeakReference
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


/**
 * X5DownloadListener
 *
 * @author why
 * @since 2023/1/7
 */
open class X5DownloadListener(private val x5WebView: X5WebView) : DownloadListener {

    override fun onDownloadStart(
        url: String,
        userAgent: String,
        contentDisposition: String,
        mimeType: String,
        contentLength: Long
    ) {
        X5OpenActionHelper.lastAskDialog?.get()?.dismiss()
        X5OpenActionHelper.lastAskDialog?.clear()

        val fileName = contentDisposition.fetchFileName(url, mimeType)
        val fileSize = ConvertUtils.byte2FitMemorySize(contentLength, 2)

        LogUtils.e("下载文件请求：FileName: $fileName, Url: $url")

        AlertDialog.Builder(x5WebView.context)
            .setMessage("该网页请求下载文件，是否允许？\n\n$fileName\n文件大小：$fileSize")
            .setPositiveButton("允许") { _, _ ->
                startDownload(url, mimeType, fileName)
            }
            .setNegativeButton("取消", null)
            .create().also { X5OpenActionHelper.lastAskDialog = WeakReference(it) }
            .show()
    }

    /**
     * 开始下载
     */
    private fun startDownload(url: String, mimeType: String, fileName: String) {
        ToastUtils.showShort("开始下载：$fileName")

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

    /**
     * 解析文件名称
     */
    private fun String.fetchFileName(url: String, mimeType: String): String {
        val strings = split(";")

        // name="xxx"
        val nameDesc = strings
            .firstOrNull { it.contains("name=", true) }
            .let {
                runCatching { URLDecoder.decode(it, StandardCharsets.UTF_8.name()) }.getOrNull()
            }
            .orEmpty()
            .trim()

        // name 字段名字
        val name = "\"(.*?)\"".toRegex().find(nameDesc)?.groupValues?.getOrNull(1)
            .orEmpty().trim().ifBlank { nameDesc.substringAfter("=") }

        // filename="xxx"
        val fileNameDesc = strings
            .firstOrNull { it.contains("filename=", true) || it.contains("filename*=", true) }
            .let {
                runCatching { URLDecoder.decode(it, StandardCharsets.UTF_8.name()) }.getOrNull()
            }
            .orEmpty()
            .trim()

        // 文件名
        val fileName = "\"(.*?)\"".toRegex().find(fileNameDesc)?.groupValues?.getOrNull(1)
            .orEmpty().trim().ifBlank { fileNameDesc.substringAfter("=") }


        // Url 截取名称
        val urlName = url.toSafeUri().pathSegments.lastOrNull().orEmpty()

        // 默认兜底名称
        val extensionName = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType).orEmpty()
        val defaultName = "${System.currentTimeMillis()}.$extensionName"

        return fileName.ifBlank { urlName }.ifBlank { name }.ifBlank { defaultName }
    }
}
