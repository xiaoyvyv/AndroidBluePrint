package com.xiaoyv.webview.helper

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Environment
import androidx.core.content.getSystemService
import com.blankj.utilcode.util.*
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebView
import com.xiaoyv.webview.listener.OnTbsListener
import com.xiaoyv.webview.utils.toSafeUri

/**
 * X5InstallHelper
 *
 * @author why
 * @since 2023/1/8
 */
object X5InstallHelper {
    private const val defaultTbsUrl: String =
        "https://tbs.imtt.qq.com/others/release/x5/tbs_core_046141_20220915165042_nolog_fs_obfs_arm64-v8a_release.tbs"

    /**
     * TBS 安装成功的 Code
     */
    const val INSTALL_SUCCESS_CODE = 200

    private const val DOWNLOAD_TMP = ".tmp-tbs.apk"

    private val downloadTbsTmpPath by lazy {
        PathUtils.getExternalAppFilesPath() + "/Download/$DOWNLOAD_TMP"
    }

    val downloadTbsPath by lazy {
        PathUtils.getExternalAppFilesPath() + "/Download/tbs-local.apk"
    }

    private var cacheDownloadId: Long = 0

    private var downloadSuccess: (String) -> Unit = {}

    /**
     * 下载完成
     */
    private val downloadBroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)

                LogUtils.e("下载完成：$downloadId, cacheDownloadId = $cacheDownloadId")

                ThreadUtils.getCachedPool().submit {
                    fetchDownloadInfo(downloadId)
                }
            }
        }

        @Synchronized
        private fun fetchDownloadInfo(downloadId: Long) {
            if (downloadId <= 0 || cacheDownloadId != downloadId) {
                FileUtils.delete(downloadTbsTmpPath)
                return
            }

            if (FileUtils.isFileExists(downloadTbsTmpPath)) {
                FileUtils.copy(downloadTbsTmpPath, downloadTbsPath)
                FileUtils.delete(downloadTbsTmpPath)
            }

            if (FileUtils.isFileExists(downloadTbsPath)) {
                LogUtils.e("下载保存路径：$downloadTbsPath")
                downloadSuccess.invoke(downloadTbsPath)
            }
        }
    }

    @JvmStatic
    fun init(appContext: Context, initWithoutWifi: Boolean = true) {
        // 在调用 TBS 初始化、创建 WebView 之前进行如下配置
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
        QbSdk.setDownloadWithoutWifi(initWithoutWifi)

        QbSdk.initX5Environment(appContext, object : QbSdk.PreInitCallback {
            /**
             * 预初始化结束
             * 由于X5内核体积较大，需要依赖网络动态下发，所以当内核不存在的时候，默认会回调 false，此时将会使用系统内核代替
             *
             * @param isX5 是否使用X5内核
             */
            override fun onViewInitFinished(isX5: Boolean) {
                LogUtils.e("X5WebView: 预初始化结束，是否使用X5内核 => $isX5")
                val x5CrashInfo = WebView.getCrashExtraMessage(appContext)
                if (isX5.not()) {
                    LogUtils.e("X5 错误原因：$x5CrashInfo")
                }
            }

            /**
             * 内核初始化完成，可能为系统内核，也可能为系统内核
             */
            override fun onCoreInitFinished() {
                LogUtils.e("X5WebView: 内核初始化完成")
            }
        })

        // 注册下载监听广播
        appContext.registerReceiver(
            downloadBroadcastReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    @JvmStatic
    @Synchronized
    fun downloadTbs(
        tbsUrl: String = defaultTbsUrl,
        useCache: Boolean = true,
        downloadSuccess: (String) -> Unit = {}
    ) {
        this.downloadSuccess = downloadSuccess

        val downloadManager = Utils.getApp().getSystemService<DownloadManager>() ?: return
        val saveDir = FileUtils.getDirName(downloadTbsTmpPath)

        // 删除缓存
        FileUtils.delete(downloadTbsTmpPath)

        if (cacheDownloadId != 0L) {
            downloadManager.remove(cacheDownloadId)
            cacheDownloadId = 0
        }

        // 本地有缓存直接发送下载成功的 Action
        if (FileUtils.isFileExists(downloadTbsPath) && useCache) {
            downloadSuccess.invoke(downloadTbsPath)
            return
        }

        LogUtils.e("开始下载")

        FileUtils.deleteAllInDir(saveDir)
        FileUtils.createOrExistsDir(saveDir)
        FileUtils.delete(downloadTbsPath)

        val uri = tbsUrl.toSafeUri()

        val request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        request.setAllowedOverRoaming(true)
        request.setMimeType("application/vnd.android.package-archive")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
        request.setDestinationInExternalFilesDir(
            Utils.getApp(),
            Environment.DIRECTORY_DOWNLOADS,
            DOWNLOAD_TMP
        )

        // 执行
        cacheDownloadId = downloadManager.enqueue(request)
    }

    @JvmStatic
    fun installByLocal(apkPath: String, tbsVersion: Int, callback: ((Int) -> Unit)? = null) {
        QbSdk.setTbsListener(object : OnTbsListener {
            override fun onInstallFinish(status: Int) {
                callback?.invoke(status)
                return
            }
        })

        if (FileUtils.isFileExists(apkPath)) {
            QbSdk.reset(Utils.getApp())
            QbSdk.installLocalTbsCore(Utils.getApp(), tbsVersion, apkPath)
            return
        }

        callback?.invoke(-1)
    }
}