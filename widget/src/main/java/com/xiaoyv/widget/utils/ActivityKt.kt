package com.xiaoyv.widget.utils

import android.app.Activity
import android.content.ContextWrapper
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
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
    return null
}

/**
 * 判断 Fragment 是否可以显示
 */
fun DialogFragment.canShow(fragmentManager: FragmentManager, tag: String? = null): Boolean {
    if (isAdded || isRemoving || isVisible) {
        return false
    }
    if (tag != null && fragmentManager.findFragmentByTag(tag) != null) {
        return false
    }
    return true
}
