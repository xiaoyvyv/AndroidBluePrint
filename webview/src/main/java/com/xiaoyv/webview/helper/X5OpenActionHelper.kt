package com.xiaoyv.webview.helper

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.IntentUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.tencent.smtt.sdk.WebView
import com.xiaoyv.webview.utils.toSafeUri
import java.lang.ref.WeakReference

/**
 * X5OpenActionHelper
 *
 * @author why
 * @since 2023/1/7
 */
object X5OpenActionHelper {
    internal var lastAskDialog: WeakReference<AlertDialog>? = null

    /**
     * 网页请求打开 App
     */
    fun showCanOpenAppDialog(webView: WebView, targetAppUri: Uri?) {
        lastAskDialog?.get()?.dismiss()
        lastAskDialog?.clear()

        val currentUri = webView.url.toSafeUri()
        val currentUrl = currentUri.scheme + "://" + currentUri.host


        // 目标 Intent
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = targetAppUri
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        if (IntentUtils.isIntentAvailable(intent)) {
            AlertDialog.Builder(webView.context)
                .setMessage("$currentUrl 请求打开 App，是否允许？")
                .setPositiveButton("允许") { _, _ ->
                    runCatching {
                        ActivityUtils.startActivity(intent)
                    }.onFailure {
                        ToastUtils.showShort("打开失败，目标应用不存在")
                    }
                }
                .setNegativeButton("取消", null)
                .create().also { lastAskDialog = WeakReference(it) }
                .show()
            return
        }

        LogUtils.e("无法处理请求意图：${targetAppUri.toString()}")
    }
}