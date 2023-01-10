package com.xiaoyv.blueprint.activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.PermissionUtils.SimpleCallback
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

/**
 * 请求日历权限
 */
fun AppCompatActivity.runWithCalendarPermission(
    fail: () -> Unit = {},
    block: () -> Unit
) {
    val granted = PermissionUtils.isGranted(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )
    if (granted) {
        runCatching { block.invoke() }.onFailure { fail.invoke() }
        return
    }

    lifecycleScope.launch {
        val result = callbackFlow {
            PermissionUtils.permissionGroup(PermissionConstants.CALENDAR)
                .callback(object : SimpleCallback {
                    override fun onGranted() {
                        trySend(true)
                        close()
                    }

                    override fun onDenied() {
                        trySend(false)
                        close()
                    }
                }).request()
            awaitClose()
        }.single()

        if (result) {
            runCatching { block.invoke() }.onFailure { fail.invoke() }
        } else {
            fail.invoke()
        }
    }
}