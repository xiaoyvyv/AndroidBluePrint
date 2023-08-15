package com.xiaoyv.widget.webview.utils

import android.app.Activity
import android.content.ContextWrapper
import android.net.Uri
import android.view.View
import com.blankj.utilcode.util.ActivityUtils

/**
 * StringKt
 *
 * @author why
 * @since 2023/1/7
 */

internal fun String?.toSafeUri(): Uri {
    return runCatching { Uri.parse(this.orEmpty()) }.getOrDefault(Uri.EMPTY)
}

/**
 * 根据 View 获取 Activity
 */
fun View.getActivity(): Activity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return ActivityUtils.getTopActivity()
}
