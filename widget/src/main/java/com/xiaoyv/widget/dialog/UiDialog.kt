package com.xiaoyv.widget.dialog

import androidx.fragment.app.FragmentActivity

/**
 * UiDialog
 *
 * @author why
 * @since 2023/4/9
 */
interface UiDialog {
    var message: String?

    fun dismiss()

    fun show(activity: FragmentActivity, msg: String? = null)
}