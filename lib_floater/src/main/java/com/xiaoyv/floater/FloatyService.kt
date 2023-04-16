package com.xiaoyv.floater

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.WindowManager
import androidx.core.content.ContextCompat
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Created by Stardust on 2017/5/1.
 */
class FloatyService : Service() {
    private var windowManager: WindowManager? = null

    override fun onCreate() {
        super.onCreate()

        windowManager = ContextCompat.getSystemService(this, WindowManager::class.java)

        for (delegate in windows) {
            delegate.onCreate(this, windowManager)
        }
        instance = this
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null

        for (delegate in windows) {
            delegate.onServiceDestroy(this)
        }
    }

    companion object {
        private val windows = CopyOnWriteArraySet<FloatyWindow>()
        private var instance: FloatyService? = null

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