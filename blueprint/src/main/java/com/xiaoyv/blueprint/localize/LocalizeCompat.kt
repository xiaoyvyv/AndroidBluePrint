@file:Suppress("DEPRECATION")

package com.xiaoyv.blueprint.localize

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.*

fun Configuration.setLocaleCompat(newLocale: Locale) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        setLocales(LocaleList(newLocale))
    } else {
        locale = newLocale
    }
}

fun Context.getLocaleCompat(): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales[0]
    } else {
        resources.configuration.locale
    }
}