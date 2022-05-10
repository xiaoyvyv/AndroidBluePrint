package com.xiaoyv.widget.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.KeyboardUtils

/**
 * 软键盘监听
 */
inline fun Activity.registerSoftInputChangedListener(
    crossinline onSoftInputChanged: (height: Int, isShow: Boolean) -> Unit = { _, _ -> },
) {
    val listener = KeyboardUtils.OnSoftInputChangedListener {
        onSoftInputChanged.invoke(it, it != 0)
    }
    KeyboardUtils.registerSoftInputChangedListener(this, listener)
}

fun Context.isDestroyed(): Boolean {
    if (this is Activity) {
        return isDestroyed || isFinishing
    }
    return false
}

/**
 * 根据 View 获取 Activity
 */
fun View.getActivity(): Activity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return ActivityUtils.getTopActivity()
}

/**
 * 根据 View 获取 FragmentActivity
 */
fun View.getFragmentActivity(): FragmentActivity? {
    return getActivity() as? FragmentActivity
}

/**
 * 判断 DialogFragment 是否可以显示
 */
fun DialogFragment.canShowInFragment(fragment: Fragment): Boolean {
    if (isAdded || isRemoving || isVisible || fragment.childFragmentManager.isDestroyed) {
        return false
    }
    return true
}

/**
 * 判断 DialogFragment 是否可以显示
 */
fun DialogFragment.canShowInActivity(fragmentActivity: FragmentActivity): Boolean {
    if (isAdded || isRemoving || isVisible || fragmentActivity.supportFragmentManager.isDestroyed) {
        return false
    }
    return true
}
