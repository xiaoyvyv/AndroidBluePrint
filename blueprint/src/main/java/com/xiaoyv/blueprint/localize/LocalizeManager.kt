package com.xiaoyv.blueprint.localize

import android.content.Context
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.Utils
import com.xiaoyv.blueprint.BluePrint
import com.xiaoyv.blueprint.base.rxjava.subscribes
import io.reactivex.rxjava3.core.Observable
import java.io.File
import java.util.*

/**
 * LocalizeManager 多语言 配置|切换
 *
 * @author why
 * @since 2021/12/14
 */
object LocalizeManager {

    /**
     * 多语言配置文件，SP 储存切换语言会导致软重启 多进程失效问题
     */
    private val languageConfigFile: String
        get() = Utils.getApp().filesDir.absolutePath + File.separator + "config" + File.separator + "language.ini"

    /**
     * 缓存当前配置文件的语言类型，避免每次读取读取配置文件
     */
    private var cacheType: Int? = null


    @LanguageType
    val configLanguageType: Int
        get() = cacheType ?: queryConfigLanguageType()

    /**
     * 获取当前配置的时区
     */
    @JvmStatic
    val configLanguageLocale: Locale
        get() = LocalizeLanguage.locale

    /**
     * 获取当前配置的语言
     */
    @JvmStatic
    @LanguageType
    private fun queryConfigLanguageType(): Int {
        FileUtils.createOrExistsFile(languageConfigFile)
        val currentConfigLanguage = FileIOUtils.readFile2String(languageConfigFile).toIntOrNull()
            ?: LanguageType.LANGUAGE_SYSTEM
        this.cacheType = currentConfigLanguage
        return currentConfigLanguage
    }

    /**
     * 配置多语言，需要在 BaseActivity 重写传入该包装
     */
    @JvmStatic
    fun Context.attachBaseContextWithLanguage(): Context {
        return LocalizeLanguage.attachBaseContext(this, configLanguageType)
    }

    /**
     * 切换语言
     */
    @JvmStatic
    fun switchLanguage(@LanguageType languageType: Int) {
        Observable.create<Boolean> {
            FileIOUtils.writeFileFromString(languageConfigFile, languageType.toString())

            // 清除当前语言类型缓存
            cacheType = null

            it.onNext(true)
            it.onComplete()
        }.compose(BluePrint.schedulerTransformer())
            .subscribes {
                AppUtils.relaunchApp(true)
            }
    }
}