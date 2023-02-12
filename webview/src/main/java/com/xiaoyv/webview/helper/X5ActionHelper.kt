package com.xiaoyv.webview.helper

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.*
import com.tencent.smtt.sdk.MimeTypeMap
import com.tencent.smtt.sdk.WebView
import com.xiaoyv.webview.utils.toSafeUri
import java.lang.ref.WeakReference
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * X5ActionHelper
 *
 * @author why
 * @since 2023/1/7
 */
object X5ActionHelper {
    var lastAskDialog: WeakReference<Dialog>? = null

    /**
     * 网页请求打开 App
     */
    fun showCanOpenAppDialog(webView: WebView, targetAppLink: String) {
        lastAskDialog?.get()?.dismiss()
        lastAskDialog?.clear()

        val currentUri = webView.url.toSafeUri()
        val currentUrl = currentUri.scheme + "://" + currentUri.host

        // 目标 Intent
        val intent = Intent.parseUri(targetAppLink, Intent.URI_ALLOW_UNSAFE)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        if (IntentUtils.isIntentAvailable(intent)) {
            val alertDialog = AlertDialog.Builder(Utils.getApp())
                .setMessage("$currentUrl 请求打开 App，是否允许？")
                .setPositiveButton("允许") { _, _ ->
                    runCatching {
                        ActivityUtils.startActivity(intent)
                    }.onFailure {
                        ToastUtils.showShort("打开失败，目标应用不存在")
                    }
                }
                .setNegativeButton("取消", null)
                .create()
            alertDialog.show()

            lastAskDialog = WeakReference(alertDialog)
            return
        }

        LogUtils.e("无法处理请求意图：$targetAppLink")
    }

    /**
     * 解析文件名称
     */
    fun String.fetchFileNameWithContentDisposition(url: String, mimeType: String): String {
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