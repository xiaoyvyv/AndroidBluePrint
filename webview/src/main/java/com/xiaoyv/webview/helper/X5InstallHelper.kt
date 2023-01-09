package com.xiaoyv.webview.helper

import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.xiaoyv.webview.listener.OnTbsListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

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
}