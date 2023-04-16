package com.xiaoyv.floater

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.CopyOnWriteArraySet

private const val NOTIFICATION_ID = 1
private const val CHANEL_ID: String = "com.xiaoyv.floater.FloatyService"

/**
 * Created by Stardust on 2017/5/1.
 */
class FloatyService : Service() {
    private var windowManager: WindowManager? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        windowManager = ContextCompat.getSystemService(this, WindowManager::class.java)

        for (delegate in windows) {
            delegate.onCreate(this, windowManager)
        }
        instance = this
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForeground(intent: Intent) {
        startForeground(NOTIFICATION_ID, buildNotification(intent))
    }

    private fun buildNotification(intent: Intent): Notification {
        val foregroundNotificationClickClass =
            intent.getStringExtra("foregroundNotificationClickClass").orEmpty()
        val foregroundNotificationChannelName =
            intent.getStringExtra("foregroundNotificationChannelName").orEmpty()
        val foregroundNotificationTitle =
            intent.getStringExtra("foregroundNotificationTitle").orEmpty()
        val foregroundNotificationText =
            intent.getStringExtra("foregroundNotificationText").orEmpty()
        val foregroundNotificationIcon =
            intent.getIntExtra("foregroundNotificationIcon", 0)

        createNotificationChannel(foregroundNotificationChannelName)

        val targetIntent = Intent(this, Class.forName(foregroundNotificationClickClass))
        val contentIntent = PendingIntent.getActivity(
            this, 0,
            targetIntent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANEL_ID)
            .setContentTitle(foregroundNotificationTitle)
            .setContentText(foregroundNotificationText)
            .setSmallIcon(foregroundNotificationIcon)
            .setWhen(System.currentTimeMillis())
            .setContentIntent(contentIntent)
            .setChannelId(CHANEL_ID)
            .setVibrate(LongArray(0))
            .build()
    }

    private fun createNotificationChannel(channelName: String) {
        val manager = ContextCompat.getSystemService(this, NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANEL_ID, channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = channelName
            channel.enableLights(false)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null

        stopForeground(STOP_FOREGROUND_REMOVE)

        for (delegate in windows) {
            delegate.onServiceDestroy(this)
        }
    }

    companion object {
        private val windows = CopyOnWriteArraySet<FloatyWindow>()
        private var instance: FloatyService? = null

        var clickIntentClass: Class<*>? = null

        @JvmStatic
        fun start(
            context: Context,
            foregroundNotificationClickClass: String,
            foregroundNotificationChannelName: String = "前台服务通知",
            foregroundNotificationTitle: String = "服务保持运行中",
            foregroundNotificationText: String = "点击进入主界面",
            foregroundNotificationIcon: Int = 0,
        ) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, FloatyService::class.java).apply {
                    putExtra("foregroundNotificationChannelName", foregroundNotificationChannelName)
                    putExtra("foregroundNotificationClickClass", foregroundNotificationClickClass)
                    putExtra("foregroundNotificationTitle", foregroundNotificationTitle)
                    putExtra("foregroundNotificationText", foregroundNotificationText)
                    putExtra("foregroundNotificationIcon", foregroundNotificationIcon)
                }
            )
        }

        @JvmStatic
        fun stop(context: Context) {
            context.stopService(Intent(context, FloatyService::class.java))
        }

        @JvmStatic
        fun isShowing(window: FloatyWindow): Boolean {
            return windows.contains(window)
        }

        @JvmStatic
        fun addWindow(window: FloatyWindow) {
            instance?.let {
                if (windows.add(window)) {
                    window.onCreate(instance, it.windowManager)
                }
            }
        }

        @JvmStatic
        fun removeWindow(window: FloatyWindow) {
            windows.remove(window)
        }
    }
}