package com.xiaoyv.floater.widget.kts

import android.net.Uri
import java.io.File

fun String.sizeOf(symbol: Char): Int {
    var i = 0
    forEach { if (it == symbol) i++ }
    return i
}

fun String?.toSafeUri(): Uri {
    return runCatching { Uri.parse(this.orEmpty()) }.getOrDefault(Uri.EMPTY)
}

fun String.appendSeparator(): String {
    return if (endsWith("/")) this else this + File.separator
}