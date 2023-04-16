@file:Suppress("MemberVisibilityCanBePrivate", "SpellCheckingInspection")

package com.xiaoyv.blueprint.localize

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.text.TextUtils
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.os.LocaleListCompat
import java.util.*

/**
 * 多语言工具类
 * 1.简体中文
 * 2.繁体中文（不区分香港和台湾）
 * 3.英文
 * 4.越南语
 * 5.泰语
 * 6.阿拉伯语
 *
 *
 * --- 1.需要实现 Application 和 Activity 的 attachBaseContext() ---
 *
 *
 * protected void attachBaseContext(Context base) {
 *
 *
 * super.attachBaseContext(BLLanguageUtils.attachBaseContext(base));
 * }
 *
 *
 * --- 1.1 切换语言时若不杀进程，则需要在切换的 Activity 重新设置 ApplicationContext 的语言环境
 *
 *
 * ...
 * 保存语言配置
 * ...
 * BLLanguageUtils.setLocale(getApplicationContext(), languageType);
 * BLAppUtils.relaunchApp(false);
 * ...
 *
 *
 * --- 2.创建文件夹 ---
 * values
 * values-ar
 * values-en
 * values-fr
 * values-it
 * values-th
 * values-vi
 * values-zh
 * values-zh-rTW
 *
 * @author why
 * @date 2021/01/15
 */
object LocalizeLanguage {
    /**
     * 克隆一份未修改的系统语言环境单例，留作切换到系统语言用
     */
    private var LOCALE_SYSTEM = Locale.getDefault().clone() as? Locale

    /**
     * 简体中文
     */
    private const val SCRIPT_HANS = "Hans"

    /**
     * 繁体中文
     */
    private const val SCRIPT_HANT = "Hant"

    val locale: Locale
        get() = LocaleListCompat.getAdjustedDefault()[0]!!

    /**
     * 获取当前手机系统的语言类型
     */
    @get:LanguageType
    val languageType: Int
        get() = getLanguageTypeByLocal(locale)

    /**
     * 注意：由于华为手机上可以同时设置语言与地区。比如：简体中文+大陆台湾。需要通过script字段进行区分简体与繁体。
     * 判断时优先判断script，再判断country字段
     *
     * @param locale locale
     */
    private fun getLanguageTypeByLocal(locale: Locale): Int {
        val language = locale.language
        val country = locale.country
        val script = locale.script
        return when (language) {
            "zh" -> if (!TextUtils.isEmpty(script) && SCRIPT_HANS == script) {
                LanguageType.LANGUAGE_ZH_HANS
            } else if (!TextUtils.isEmpty(script) && SCRIPT_HANT == script) {
                LanguageType.LANGUAGE_ZH_HANT
            } else if ("HK" == country || "MO" == country || "TW" == country) {
                LanguageType.LANGUAGE_ZH_HANT
            } else {
                LanguageType.LANGUAGE_ZH_HANS
            }
            "th" -> LanguageType.LANGUAGE_TH
            "ar" -> LanguageType.LANGUAGE_AR
            "vi" -> LanguageType.LANGUAGE_VI
            "fr" -> LanguageType.LANGUAGE_FR
            "it" -> LanguageType.LANGUAGE_IT
            "en" -> LanguageType.LANGUAGE_EN
            else -> LanguageType.LANGUAGE_SYSTEM
        }
    }

    private fun createLocaleByType(@LanguageType languageType: Int): Locale {
        return when (languageType) {
            LanguageType.LANGUAGE_ZH_HANS -> Locale.CHINESE
            LanguageType.LANGUAGE_ZH_HANT -> Locale.TRADITIONAL_CHINESE
            LanguageType.LANGUAGE_AR -> Locale("ar")
            LanguageType.LANGUAGE_TH -> Locale("th")
            LanguageType.LANGUAGE_VI -> Locale("vi")
            LanguageType.LANGUAGE_FR -> Locale("fr")
            LanguageType.LANGUAGE_IT -> Locale("it")
            LanguageType.LANGUAGE_EN -> Locale.ENGLISH
            LanguageType.LANGUAGE_SYSTEM -> LOCALE_SYSTEM!!
            else -> LOCALE_SYSTEM!!
        }
    }

    /**
     * 需要在 Application 和 Activity 都使用
     */
    fun attachBaseContext(context: Context, @LanguageType languageType: Int): Context {
        // 更新
        var tempContext = updateResources(context, languageType)

        if (tempContext is Activity) {
            tempContext = warpActivityContext(tempContext)
        }
        return tempContext
    }

    /**
     * 更新 Context 的语言资源
     *
     * @param context context
     * @param languageType 语言类型
     */
    private fun updateResources(context: Context, languageType: Int): Context {
        val newLocale = createLocaleByType(languageType)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(context.resources.configuration.also {
                it.setLocaleCompat(newLocale)
            })
        } else {
            context.also {
                it.resources.configuration.setLocaleCompat(newLocale)
            }
        }
    }

    /**
     * 修复 AppCompat v1.2.0 以上界面多语言混乱问题
     *
     * @param context context
     * @return 包装 context
     */
    private fun warpActivityContext(context: Context): Context {
        return object : ContextThemeWrapper(context, context.theme) {
            override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
                overrideConfiguration?.setTo(context.resources.configuration)
                super.applyOverrideConfiguration(overrideConfiguration)
            }
        }
    }
}