@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.calendar.ics

import com.xiaoyv.calendar.CalendarAccount
import com.xiaoyv.calendar.CalendarEvent
import java.text.SimpleDateFormat
import java.util.*

/**
 * CisCreator
 *
 * @author why
 * @since 2023/1/11
 */
object IcsCreator {

    /**
     * 创建 CIS 文件内容
     */
    fun createIcsContent(
        calendarAccount: CalendarAccount,
        calendarEvents: List<CalendarEvent>
    ): String {
        return buildString {
            // 头部
            append("BEGIN:VCALENDAR\n")
            append("PRODID:-//${calendarAccount.displayName}//${calendarAccount.accountName}//CN\n")
            append("VERSION:2.0\n")
            append("CALSCALE:GREGORIAN\n")
            append("METHOD:PUBLISH\n")
            append("X-WR-CALNAME:${calendarAccount.displayName}\n")
            append("X-WR-TIMEZONE:Asia/Shanghai\n")
            append("X-WR-CALDESC:${calendarAccount.displayName}，共计 ${calendarEvents.size} 个日程事件\n")

            // 事件内容
            calendarEvents.forEach {
                append("BEGIN:VEVENT\n")
                append("DTSTART:${it.startTime.toUtcTime()}\n")
                append("DTEND:${it.endTime.toUtcTime()}\n")
                append("DTSTAMP:${System.currentTimeMillis().toUtcTime()}\n")
                append("UID:${it.hashCode()}::${it.startTime}::${it.endTime}\n")
                append("CREATED:${System.currentTimeMillis().toUtcTime()}\n")
                append("DESCRIPTION:${it.description.replace("\n", "\\n")}\n")
                append("LAST-MODIFIED:${System.currentTimeMillis().toUtcTime()}\n")
                append("LOCATION:${it.eventLocation.replace("\n", "\\n")}\n")
                append("SEQUENCE:0\n")
                append("STATUS:CONFIRMED\n")
                append("SUMMARY:${it.eventName}\n")
                append("TRANSP:OPAQUE\n")
                append("BEGIN:VALARM\n")
                append("ACTION:DISPLAY\n")
                append("DESCRIPTION:提醒事项\n")
                append("TRIGGER:-P0DT0H${it.minutes}M0S\n")
                append("END:VALARM\n")
                append("END:VEVENT\n")
            }
            // 尾部
            append("END:VCALENDAR")
        }
    }

    /**
     * 转为 UTC 时间
     */
    private fun Long.toUtcTime(): String {
        runCatching {
            val format = SimpleDateFormat("yyyyMMdd'T'HHmmssZ", Locale.getDefault())
            return format.format(this)
        }
        return ""
    }
}