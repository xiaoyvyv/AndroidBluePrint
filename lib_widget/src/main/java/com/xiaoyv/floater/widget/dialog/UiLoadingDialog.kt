@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.xiaoyv.floater.widget.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ColorUtils
import com.xiaoyv.floater.widget.R
import com.xiaoyv.floater.widget.databinding.UiDialogLoadingBinding
import com.xiaoyv.floater.widget.kts.DrawableUtils
import com.xiaoyv.floater.widget.kts.canShowInActivity
import com.xiaoyv.floater.widget.kts.canShowInFragment
import com.xiaoyv.floater.widget.kts.dpi

/**
 * UiLoadingDialog
 *
 * @author why
 * @since 2021/12/13
 */
class UiLoadingDialog : DialogFragment(), UiDialog {
    private var fragmentTag = javaClass.simpleName

    override var message: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return UiDialogLoadingBinding.inflate(LayoutInflater.from(context)).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        refreshView(view)
    }

    private fun refreshView(view: View?) {
        val binding = UiDialogLoadingBinding.bind(view ?: return)

        binding.root.background = DrawableUtils.createSolidDrawable(
            ColorUtils.getColor(R.color.ui_black_90),
            4.dpi.toFloat()
        )

        // 设置文案
        message?.let {
            binding.tvMsg.text = it
        }
    }

    override fun show(activity: FragmentActivity, msg: String?) {
        if (canShowInActivity(activity)) {
            this.message = msg
            showNow(activity.supportFragmentManager, fragmentTag)
        }
    }

    fun show(fragment: Fragment, msg: String? = null) {
        if (canShowInFragment(fragment)) {
            this.message = msg
            showNow(fragment.childFragmentManager, fragmentTag)
        }
    }

    override fun dismiss() {
        if (isAdded) {
            super.dismissAllowingStateLoss()
        }
    }

    override fun onStart() {
        super.onStart()

        val window = dialog?.window ?: return

        window.setBackgroundDrawableResource(R.color.ui_transparent)
        window.attributes = window.attributes.apply {
            dimAmount = 0.1F
            width = 150.dpi
        }

        refreshView(view)
    }
}