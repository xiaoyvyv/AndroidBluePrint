package com.xiaoyv.widget.utils

import android.net.Uri

/**
 * StringKt
 *
 * @author why
 * @since 2023/1/7
 */

fun String?.toSafeUri(): Uri {
    return runCatching { Uri.parse(this.orEmpty()) }.getOrDefault(Uri.EMPTY)
}