@file:Suppress("HasPlatformType")

package com.xiaoyv.widget.kts

import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.PowerManager
import android.os.storage.StorageManager
import android.view.inputmethod.InputMethodManager
import androidx.core.app.NotificationManagerCompat
import com.blankj.utilcode.util.Utils
import java.util.concurrent.Executor

val application get() = Utils.getApp()

val contentResolver: ContentResolver by lazy { application.contentResolver }

val mainExecutor: Executor by lazy { application.mainExecutorCompat }

val packageManager: PackageManager by lazy { application.packageManager }

val clipboardManager: ClipboardManager by lazy {
    application.getSystemServiceCompat(ClipboardManager::class.java)
}

val inputMethodManager: InputMethodManager by lazy {
    application.getSystemServiceCompat(InputMethodManager::class.java)
}

val notificationManager: NotificationManagerCompat by lazy {
    NotificationManagerCompat.from(application)
}

val powerManager: PowerManager by lazy {
    application.getSystemServiceCompat(PowerManager::class.java)
}

val storageManager: StorageManager by lazy {
    application.getSystemServiceCompat(StorageManager::class.java)
}

val wifiManager: WifiManager by lazy {
    application.getSystemServiceCompat(WifiManager::class.java)
}
