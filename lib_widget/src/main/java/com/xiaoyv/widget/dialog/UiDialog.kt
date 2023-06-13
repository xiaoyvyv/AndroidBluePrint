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

    var canCancelable: Boolean

    fun dismiss()

    fun show(activity: FragmentActivity, msg: String? = null)

    fun addOnShowListener(showListener: OnShowListener)
    fun addOnDismissListener(dismissListener: OnDismissListener)

    interface OnDismissListener {
        fun onDismiss(dialog: UiDialog)
    }

    interface OnShowListener {
        fun onShow(dialog: UiDialog)
    }
}