package com.xiaoyv.calendar

import java.io.Serializable

/**
 * CalendarEvent
 *
 * @author why
 * @since 2022/7/8
 */
data class CalendarEvent(
    var eventName: String = "",
    var startTime: Long = 0,
    var endTime: Long = 0,
    var description: String = "",
    var eventLocation: String = "",
    var minutes: Int = 15
) : Serializable
