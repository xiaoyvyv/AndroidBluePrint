package com.xiaoyv.widget.kts

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ActivityUtils
import java.util.concurrent.Executor

fun Context.isDestroyed(): Boolean {
    if (this is Activity) {
        return isDestroyed || isFinishing
    }
    return false
}

val Context?.fetchActivity: Activity?
    get() {
        val ctx = this ?: return ActivityUtils.getTopActivity()
        var context = ctx
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

val Context?.fetchFragmentActivity: FragmentActivity?
    get() = fetchActivity as? FragmentActivity


/**
 * 判断 DialogFragment 是否可以显示
 */
fun DialogFragment.canShowInActivity(fragmentActivity: FragmentActivity): Boolean {
    return !(isAdded || isRemoving || isVisible || fragmentActivity.supportFragmentManager.isDestroyed)
}

val Context.mainExecutorCompat: Executor
    get() = ContextCompat.getMainExecutor(this)

fun <T> Context.getSystemServiceCompat(serviceClass: Class<T>): T =
    ContextCompat.getSystemService(this, serviceClass)!!

