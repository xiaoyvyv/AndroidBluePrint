package com.xiaoyv.blueprint.localize

import androidx.annotation.IntDef

/**
 * 多语言支持类型
 */
@IntDef(
    LanguageType.LANGUAGE_SYSTEM,
    LanguageType.LANGUAGE_ZH_HANS,
    LanguageType.LANGUAGE_ZH_HANT,
    LanguageType.LANGUAGE_EN,
    LanguageType.LANGUAGE_VI,
    LanguageType.LANGUAGE_TH,
    LanguageType.LANGUAGE_AR,
    LanguageType.LANGUAGE_FR,
    LanguageType.LANGUAGE_IT
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class LanguageType {
    companion object {
        /**
         * 跟随系统
         */

        const val LANGUAGE_SYSTEM = 0

        /**
         * 简体中文
         */

        const val LANGUAGE_ZH_HANS = 1

        /**
         * 繁体中文（不区分香港和台湾）
         */

        const val LANGUAGE_ZH_HANT = 2

        /**
         * 英文（默认）
         */

        const val LANGUAGE_EN = 3

        /**
         * 越南语
         */

        const val LANGUAGE_VI = 4

        /**
         * 泰文
         */

        const val LANGUAGE_TH = 5

        /**
         * 阿拉伯语
         */

        const val LANGUAGE_AR = 6

        /**
         * 法语
         */

        const val LANGUAGE_FR = 7

        /**
         * 意大利语
         */
        const val LANGUAGE_IT = 8
    }
}