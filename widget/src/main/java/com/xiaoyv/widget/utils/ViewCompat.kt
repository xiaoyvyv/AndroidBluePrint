package com.xiaoyv.widget.utils

import android.os.Build
import android.widget.TextView
import androidx.annotation.AnyRes

/**
 * ViewCompat
 *
 * @author why
 * @since 2022/2/27
 */
@Suppress("DEPRECATION")
fun TextView.setTextAppearanceCompat(@AnyRes resId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setTextAppearance(resId)
    } else {
        setTextAppearance(context, resId)
    }
}