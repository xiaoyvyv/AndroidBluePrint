package com.xiaoyv.widget.dialog.loading

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.StringUtils
import com.xiaoyv.widget.R
import com.xiaoyv.widget.databinding.UiViewLoadingBinding
import com.xiaoyv.widget.utils.DrawableUtils

/**
 * LoadingDialog
 *
 * @author why
 * @since 2021/01/08
 */
class UiLoadingDialog : DialogFragment() {
    private var msg: String? = null

    var cancel = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return UiViewLoadingBinding.inflate(inflater, container, false).root
    }

    override fun onStart() {
        super.onStart()
        val binding = UiViewLoadingBinding.bind(requireView())
        binding.root.background = DrawableUtils.createSolidDrawable(
            ColorUtils.getColor(R.color.ui_text_c1),
            LOADING_RADIUS_DP
        )

        binding.tvMsg.text = msg ?: StringUtils.getString(R.string.ui_view_status_loading)

        dialog?.also {
            it.setCanceledOnTouchOutside(false)
            it.setCancelable(cancel)
            it.window?.setBackgroundDrawable(ColorDrawable(ColorUtils.getColor(R.color.ui_translate)))
            it.window?.attributes = it.window?.attributes?.apply {
                dimAmount = 0.2F
                width = ConvertUtils.dp2px(LOADING_WIDTH_DP)
            }
        }
    }

    fun showWithMsg(manager: FragmentManager, msg: String?) {
        this.msg = msg
        super.show(manager, javaClass.simpleName)
    }

    override fun dismiss() {
        if (isAdded) {
            super.dismiss()
        }
    }

    companion object {
        private const val LOADING_WIDTH_DP = 200F
        private const val LOADING_RADIUS_DP = 8F
    }
}