package com.xiaoyv.webview.helper

import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.*
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.xiaoyv.webview.listener.OnTbsListener
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * TBS 默认下载路径
 */
const val TBS_DEFEAT_CORE_URL: String =
    "https://tbs.imtt.qq.com/others/release/x5/tbs_core_046141_20220915165042_nolog_fs_obfs_arm64-v8a_release.tbs"

/**
 * TBS 默认下载版本号
 */
const val TBS_DEFEAT_CORE_VERSION = 46141


/**
 * TBS 安装成功的 Code
 */
const val TBS_INSTALL_SUCCESS_CODE = 200

/**
 * 超时
 */
const val TBS_INSTALL_TIMEOUT = -100

/**
 * 文件不存在
 */
const val TBS_INSTALL_FILE_NOT_FOUND = -101

/**
 * X5InstallHelper
 *
 * @author why
 * @since 2023/1/8
 */
object X5InstallHelper {

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
            }

            /**
             * 内核初始化完成，可能为系统内核，也可能为系统内核
             */
            override fun onCoreInitFinished() {
                LogUtils.e("X5WebView: 内核初始化完成")
            }
        })
    }

    /**
     * 下载 TBS
     *
     * @return 返回下载成功的文件路径
     */
    @Throws(IOException::class)
    suspend fun download(
        tbsUrl: String = TBS_DEFEAT_CORE_URL,
        tbsVersion: Int = TBS_DEFEAT_CORE_VERSION,
        progressChange: (Float, Long, Long) -> Unit = { _, _, _ -> },
    ): String {
        // 创建文件夹
        val downloadDir = PathUtils.getExternalAppFilesPath() + "/Download"
        val downloadTmpDir = PathUtils.getExternalAppFilesPath() + "/Download/.tmp"
        FileUtils.createOrExistsDir(downloadDir)
        FileUtils.createOrExistsDir(downloadTmpDir)

        // 本地路径
        val urlKey = EncryptUtils.encryptMD5ToString(tbsUrl).lowercase()
        val tbsFileName = "tbs_${tbsVersion}_$urlKey.apk"
        val tbsFilePath = "$downloadDir/$tbsFileName"
        val tbsTmpFilePath = "$downloadTmpDir/$tbsFileName.tmp"

        if (FileUtils.isFileExists(tbsFilePath)) {
            return tbsFilePath
        }

        withContext(Dispatchers.IO) {
            // 删除缓存
            FileUtils.createFileByDeleteOldFile(tbsTmpFilePath)

            // 网络请求
            val url = URL(tbsUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.instanceFollowRedirects = true
            connection.connect()

            // 准备读取数据下载
            val responseCode = connection.responseCode
            val maxLength = connection.contentLength.toLong()
            var tmp = 0f
            if (HttpURLConnection.HTTP_OK == responseCode && maxLength >= 0) {
                connection.inputStream.use { inputStream ->
                    FileOutputStream(tbsTmpFilePath).use { fileStream ->
                        flow {
                            // 流写入
                            val buffer = ByteArray(1024 * 16)
                            var readLength: Int
                            var downloadLength = 0
                            while (inputStream.read(buffer).also { readLength = it } > 0) {
                                fileStream.write(buffer, 0, readLength)
                                downloadLength += readLength

                                val progress = downloadLength / maxLength.toFloat()
                                emit(progress)
                            }
                        }.flowOn(Dispatchers.IO)
                            // 防抖动，间隔 500ms 回调一次进度
                            .buffer(1, BufferOverflow.DROP_OLDEST)
                            .collectIndexed { _, progress ->
                                // 计算速度
                                val downloaded = maxLength * progress
                                val speed = (downloaded - tmp).toLong()
                                tmp = downloaded

                                // 回调速度
                                withContext(Dispatchers.Main) {
                                    progressChange.invoke(
                                        progress,
                                        speed,
                                        maxLength
                                    )
                                }
                                delay(1000)
                            }
                    }
                }

                // 复制到目标路径
                FileUtils.copy(tbsTmpFilePath, tbsFilePath)

                // 删除缓存
                FileUtils.delete(tbsTmpFilePath)
                return@withContext
            }
            throw IOException("Connect fail: $responseCode, length: $maxLength")
        }
        return tbsFilePath
    }

    /**
     * 本地安装 TBS
     */
    @JvmStatic
    fun installByLocal(apkPath: String, tbsVersion: Int, callback: ((Int) -> Unit) = {}) {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            runCatching {
                withTimeout(10000) {
                    val installCode = callbackFlow {
                        QbSdk.setTbsListener(object : OnTbsListener {
                            override fun onInstallFinish(status: Int) {
                                trySend(status)
                                close()
                                return
                            }
                        })
                        if (FileUtils.isFileExists(apkPath)) {
                            QbSdk.reset(Utils.getApp())
                            QbSdk.installLocalTbsCore(Utils.getApp(), tbsVersion, apkPath)
                        } else {
                            trySend(TBS_INSTALL_FILE_NOT_FOUND)
                            close()
                        }
                        awaitClose {
                            QbSdk.setTbsListener(null)
                        }
                    }.flowOn(Dispatchers.IO).single()
                    callback.invoke(installCode)
                }
            }.onFailure {
                QbSdk.setTbsListener(null)
                callback.invoke(TBS_INSTALL_TIMEOUT)
            }
        }
    }

    /**
     * 文件写入
     *
     * @param inputStream 数据流
     * @param file        储存地址
     * @throws IOException IOException
     */
    @Throws(IOException::class)
    private fun read(inputStream: InputStream, file: File, maxLength: Long) {

    }
}