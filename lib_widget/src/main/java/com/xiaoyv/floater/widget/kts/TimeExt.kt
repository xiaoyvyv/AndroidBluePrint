@file:Suppress("unused")

package com.xiaoyv.floater.widget.kts

const val SECONDS_MIN = 60
const val SECONDS_HOUR = 60 * 60
const val SECONDS_DAY = SECONDS_HOUR * 24
const val SECONDS_MONTH = SECONDS_DAY * 30
const val SECONDS_YEAR = SECONDS_DAY * 365

/**
 * 秒 -> 时分秒
 */
fun Long.formatHMS(): String {
    val sec = this % 60
    val min = this / 60 % 60
    val hour = this / 60 / 60
    return String.format("%02d:%02d:%02d", hour, min, sec)
}