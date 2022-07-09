package com.xiaoyv.calendar

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import androidx.annotation.RequiresPermission
import java.util.*

/**
 * CalendarReminder
 *
 * @author why
 * @since 2022/7/6
 */
object CalendarReminder {
    private const val TAG = "CalendarReminder"

    /**
     * 添加日历账户
     *
     * @return 成功则返回账户id，否则返回-1
     */
    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    fun createCalendarAccount(context: Context, user: CalendarAccount): Long {
        val accountName = user.accountName
        if (accountName.isBlank()) {
            Log.e(TAG, "createCalendarAccount: accountName can`t be empty")
            return -1
        }

        // 先检测是否存在
        val calendarAccount = checkCalendarAccount(context, accountName)
        if (calendarAccount != null) {
            return calendarAccount.calendarId
        }

        val value = ContentValues()
        // 账户类型：本地
        // 在添加账户时，如果账户类型不存在系统中，则可能该新增记录会被标记为脏数据而被删除
        // 设置为ACCOUNT_TYPE_LOCAL可以保证在不存在账户类型时，该新增数据不会被删除
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
        // 日历账户的名称
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
        // 日历在表中的名称
        value.put(CalendarContract.Calendars.NAME, user.name)
        // 账户显示的名称
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, user.displayName)
        // 日历颜色
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, user.color)
        // 拥有者的账户
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, accountName)
        // 设置此日历可见
        value.put(CalendarContract.Calendars.VISIBLE, 1)
        // 可以响应事件
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 1)
        // 单个事件设置的最大的提醒数
        value.put(CalendarContract.Calendars.MAX_REMINDERS, 8)
        // 日历时区
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().id)
        // 可以修改日历时区
        value.put(CalendarContract.Calendars.CAN_MODIFY_TIME_ZONE, 1)
        // 同步此日历到设备上
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1)
        // 可以响应事件
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 1)
        // 设置允许提醒的方式
        value.put(CalendarContract.Calendars.ALLOWED_REMINDERS, "0,1,2,3,4")
        // 设置日历支持的可用性类型
        value.put(CalendarContract.Calendars.ALLOWED_AVAILABILITY, "0,1,2")
        // 设置日历允许的出席者类型
        value.put(CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES, "0,1,2")
        // 拥有者的账户
        value.put(
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
            CalendarContract.Calendars.CAL_ACCESS_OWNER
        )

        // 添加账户
        val calendarUri = CalendarContract.Calendars.CONTENT_URI.buildUpon()
            .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
            .appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL
            )
            .build()

        val result = context.contentResolver.insert(calendarUri, value)
        return if (result == null) -1 else ContentUris.parseId(result)
    }

    /**
     * 检查是否存在日历账户
     *
     * @return 存在：日历账户ID  不存在：-1
     */
    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    fun checkCalendarAccount(context: Context, accountName: String): CalendarAccount? {
        val selection = "(" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?)"
        val selectionArgs = arrayOf(accountName)

        context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI, arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR,
            ), selection, selectionArgs, null
        ).use { cursor ->
            // 不存在日历账户
            if (cursor == null || cursor.count == 0) {
                return null
            }

            // 存在日历账户，获取第一个账户的ID
            cursor.moveToFirst()
            val idIndex = cursor.getColumnIndex(CalendarContract.Calendars._ID)
            val nameIndex = cursor.getColumnIndex(CalendarContract.Calendars.NAME)
            val accountNameIndex = cursor.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)
            val colorIndex = cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR)
            val displayNameIndex = cursor.getColumnIndex(
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
            )

            if (idIndex == -1 || nameIndex == -1
                || accountNameIndex == -1
                || displayNameIndex == -1
                || colorIndex == -1
            ) {
                return null
            }

            val calendarId = cursor.getInt(idIndex).toLong()
            if (calendarId == -1L) {
                return null
            }
            return CalendarAccount().apply {
                this.calendarId = calendarId
                this.name = cursor.getString(nameIndex)
                this.accountName = cursor.getString(accountNameIndex)
                this.displayName = cursor.getString(displayNameIndex)
                this.color = cursor.getInt(colorIndex)
            }
        }
    }

    /**
     * 添加日历事件
     *
     * 插入一个新事件的规则：
     * - 必须包含 CALENDAR_ID 和 DTSTART 字段
     * - 必须包含 EVENT_TIMEZONE 字段,使用 TimeZone.getDefault().getID() 方法获取默认时区
     * - 对于非重复发生的事件,必须包含 DTEND 字段
     * - 对重复发生的事件,必须包含一个附加了 RRULE 或 RDATE 字段的 DURATION 字段
     */
    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    fun createCalendarEvent(
        context: Context,
        calendarId: Long,
        calendarEvent: CalendarEvent
    ): Long {
        val startTime = calendarEvent.startTime
        val endTime = calendarEvent.endTime
        val eventName = calendarEvent.eventName

        // 存在则删除
        val queryEventId = isEventAlreadyExist(context, startTime, endTime, eventName)
        if (queryEventId != -1L) {
            Log.e(TAG, "createCalendarEvent: Event: $eventName 存在：$queryEventId，先删除")
            deleteCalendarEvent(context, queryEventId)
        } else {
            Log.e(TAG, "createCalendarEvent: Event: $eventName 不存在，创建")
        }

        // 开始组装事件数据
        val event = ContentValues()
        // 事件要插入到的日历账户
        event.put(CalendarContract.Events.CALENDAR_ID, calendarId)
        // 事件开始时间
        event.put(CalendarContract.Events.DTSTART, startTime)
        // 事件结束时间
        event.put(CalendarContract.Events.DTEND, calendarEvent.endTime)
        // 事件标题
        event.put(CalendarContract.Events.TITLE, eventName)
        // 事件描述(对应手机系统日历备注栏)
        event.put(CalendarContract.Events.DESCRIPTION, calendarEvent.description)
        // 事件地点
        event.put(CalendarContract.Events.EVENT_LOCATION, calendarEvent.eventLocation)
        // 事件时区
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        // 定义事件的显示，默认即可
        event.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT)
        // 事件的状态
        event.put(CalendarContract.Events.STATUS, 0)
        // 设置事件提醒警报可用
        event.put(CalendarContract.Events.HAS_ALARM, 1)
        // 设置事件忙
        event.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)

        // 创建的日历事件
        val eventUri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, event)
            ?: return -1

        // 获取事件ID
        val eventId = ContentUris.parseId(eventUri)

        // 开始组装事件提醒数据
        ContentValues().apply {
            // 此提醒所对应的事件ID
            put(CalendarContract.Reminders.EVENT_ID, eventId)
            // 设置提前提醒的时间（分钟）
            put(CalendarContract.Reminders.MINUTES, calendarEvent.minutes)
            // 设置事件提醒方式为通知警报
            put(
                CalendarContract.Reminders.METHOD,
                CalendarContract.Reminders.METHOD_ALERT
            )
            // 插入提醒
            context.contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, this)
                ?: return -1
        }

        return eventId
    }

    /**
     * 删除日历事件
     *
     * @param eventId 事件ID
     */
    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    fun deleteCalendarEvent(context: Context, eventId: Long): Boolean {
        val deletedCount1: Int

        // 删除匹配条件
        val selection = "(" + CalendarContract.Events._ID + " = ?)"
        val selectionArgs = arrayOf(eventId.toString())

        deletedCount1 = context.contentResolver.delete(
            CalendarContract.Events.CONTENT_URI,
            selection,
            selectionArgs
        )

        // 删除匹配条件
        val selection2 = "(" + CalendarContract.Reminders.EVENT_ID + " = ?)"
        val selectionArgs2 = arrayOf(eventId.toString())

        val deletedCount2 = context.contentResolver.delete(
            CalendarContract.Reminders.CONTENT_URI,
            selection2,
            selectionArgs2
        )
        return (deletedCount1 + deletedCount2) > 0
    }

    /**
     * 判断日历账户中是否已经存在此事件
     *
     * @param begin 事件开始时间
     * @param end   事件结束时间
     * @param title 事件标题
     */
    @RequiresPermission(Manifest.permission.WRITE_CALENDAR)
    private fun isEventAlreadyExist(
        context: Context,
        begin: Long,
        end: Long,
        title: String
    ): Long {
        val projection = arrayOf(
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.TITLE
        )
        CalendarContract.Instances.query(
            context.contentResolver, projection, begin, end, title
        ).use { cursor ->
            // 不存在日历账户
            if (cursor == null || cursor.count == 0) {
                return -1
            }

            // 返回事件ID
            val columnIndex = cursor.getColumnIndex(CalendarContract.Instances.EVENT_ID)
            if (columnIndex == -1) {
                return -1
            }
            cursor.moveToFirst()
            return cursor.getInt(columnIndex).toLong()
        }
    }
}