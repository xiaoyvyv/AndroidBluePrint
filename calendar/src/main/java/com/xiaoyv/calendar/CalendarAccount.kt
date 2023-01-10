package com.xiaoyv.calendar

import android.graphics.Color
import androidx.annotation.ColorInt
import java.io.Serializable

/**
 * CalendarUser
 *
 * @author why
 * @since 2022/7/8
 */
data class CalendarAccount(
    var calendarId: Long = 0,
    var name: String = "",
    var accountName: String = "",
    var displayName: String = "",
    @ColorInt
    var color: Int = Color.parseColor("#FF80AB")
) : Serializable
