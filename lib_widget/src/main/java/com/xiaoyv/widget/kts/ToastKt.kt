package com.xiaoyv.widget.kts

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.Utils
import java.lang.ref.WeakReference

/**
 * @author why
 * @since 12/3/23
 */
private var toast: WeakReference<Toast>? = null

fun Fragment.toast(message: Any?) {
    toast(context, message)
}

fun Activity.toast(message: Any?) {
    toast(this, message)
}

fun showToastCompat(any: Any? = null) {
    toast(Utils.getApp(), any)
}

private fun toast(context: Context?, message: Any?) {
    toast?.get()?.cancel()
    toast?.clear()
    toast = WeakReference(
        Toast.makeText(
            context ?: Utils.getApp(),
            message.toString(),
            Toast.LENGTH_SHORT
        )
    )
    toast?.get()?.show()
}