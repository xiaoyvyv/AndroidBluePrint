package com.xiaoyv.widget.utils

import android.view.Window
import android.view.WindowManager

/**
 * WindowKt
 *
 * @author why
 * @since 2022/2/27
 */

inline var Window.isSoftInputModeAlwaysVisible: Boolean
    get() = attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
    set(value) {
        setSoftInputMode(
            if (value) WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
            else WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED
        )
    }

inline fun Window.updateWindowParams(
    block: WindowManager.LayoutParams.() -> Unit
) {
    attributes = attributes.apply {
        block(this)
    }
}
