@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.xiaoyv.widget.dialog

import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.core.view.isGone
import androidx.core.view.updatePadding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import com.xiaoyv.widget.R
import com.xiaoyv.widget.databinding.UiDialogNormalBinding
import com.xiaoyv.widget.utils.canShow
import com.xiaoyv.widget.utils.dpi

/**
 * UiNormalDialog 全局对话框
 *
 * @author why
 * @since 2021/12/23
 */
open class UiNormalDialog : DialogFragment() {
    private var fragmentTag = javaClass.simpleName
    internal var builder: Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        builder = arguments?.getParcelable(ARG_BUILDER) as? Builder ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = UiDialogNormalBinding.inflate(layoutInflater, container, false).root

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = UiDialogNormalBinding.bind(view)
        val param = builder ?: return
        val customView = param.customView

        isCancelable = param.backCancelable

        // 背景圆角
        binding.root.setCardBackgroundColor(param.background)
        binding.root.radius = param.radius.toFloat()

        // 视图相关
        binding.tvTitle.isGone = param.title.isNullOrBlank()
        binding.tvContent.isGone = param.message.isNullOrBlank()
        binding.tvCancel.isGone = param.cancelText.isNullOrBlank()
        binding.tvConfirm.isGone = param.confirmText.isNullOrBlank()
        binding.flView.isGone = customView == 0
        binding.vDivider.isGone = binding.tvCancel.isGone && binding.tvConfirm.isGone

        // 自定义视图

        if (customView != 0) {
            binding.flView.removeAllViews()
            val inflateView =
                LayoutInflater.from(requireActivity()).inflate(customView, binding.flView, false)
            if (inflateView != null) {
                param.onCustomViewInitListener.invoke(inflateView)
                binding.flView.addView(inflateView)
            }
        }

        // 标题
        binding.tvTitle.text = param.title
        binding.tvTitle.textSize = param.titleSize
        binding.tvTitle.setTextColor(param.titleColor)
        binding.tvTitle.typeface =
            if (param.titleBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        // 内容
        binding.tvContent.text = param.message
        binding.tvContent.textSize = param.messageSize
        binding.tvContent.setTextColor(param.messageColor)
        binding.tvContent.typeface =
            if (param.messageBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        binding.tvContent.updatePadding(
            top = param.messagePaddingVertical,
            bottom = param.messagePaddingVertical
        )

        // 取消键
        binding.tvCancel.text = param.cancelText
        binding.tvCancel.textSize = param.cancelTextSize
        binding.tvCancel.setTextColor(param.cancelTextColor)
        binding.tvCancel.typeface =
            if (param.cancelTextBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        // 确定键
        binding.tvConfirm.text = param.confirmText
        binding.tvConfirm.textSize = param.confirmTextSize
        binding.tvConfirm.setTextColor(param.confirmTextColor)
        binding.tvConfirm.typeface =
            if (param.confirmTextBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT

        // 点击 取消键
        binding.tvCancel.setOnClickListener {
            if (param.cancelCancelable) {
                dismiss()
            }
            param.onCancelClickListener.invoke(it)
        }
        // 点击 确定键
        binding.tvConfirm.setOnClickListener {
            if (param.confirmCancelable) {
                dismiss()
            }
            param.onConfirmClickListener.invoke(it)
        }
    }

    override fun onStart() {
        super.onStart()

        val window = dialog?.window ?: return
        val param = builder ?: return

        dialog?.setCanceledOnTouchOutside(param.touchOutsideCancelable)

        window.setBackgroundDrawableResource(R.color.ui_transparent)
        window.attributes = window.attributes.apply {
            dimAmount = param.dimAmount
            width = param.width
        }
    }

    fun show(fragmentActivity: FragmentActivity) {
        if (canShow(fragmentActivity.supportFragmentManager, fragmentTag)) {
            showNow(fragmentActivity.supportFragmentManager, fragmentTag)
        }
    }

    fun show(fragment: Fragment) {
        if (canShow(fragment.childFragmentManager, fragmentTag)) {
            showNow(fragment.childFragmentManager, fragmentTag)
        }
    }

    override fun dismiss() {
        if (isAdded) {
            super.dismissAllowingStateLoss()
        }
    }

    /**
     * 对话框建造者
     */
    open class Builder(
        var width: Int = 280.dpi,
        var radius: Int = 6.dpi,
        var background: Int = ColorUtils.getColor(R.color.ui_system_background),
        var dimAmount: Float = 0.38f,
        var cancelCancelable: Boolean = true,
        var confirmCancelable: Boolean = true,
        var touchOutsideCancelable: Boolean = true,
        var backCancelable: Boolean = true,

        var title: String? = null,
        var titleSize: Float = 18f,
        var titleBold: Boolean = true,
        var titleColor: Int = ColorUtils.getColor(R.color.ui_text_c1),

        var message: String? = null,
        var messageSize: Float = 14f,
        var messageBold: Boolean = false,
        var messagePaddingVertical: Int = 0,
        var messageColor: Int = ColorUtils.getColor(R.color.ui_text_c2),

        var cancelText: String? = StringUtils.getString(R.string.ui_common_cancel),
        var cancelTextSize: Float = 18f,
        var cancelTextColor: Int = ColorUtils.getColor(R.color.ui_text_c3),
        var cancelTextBold: Boolean = true,

        var confirmText: String? = StringUtils.getString(R.string.ui_common_done),
        var confirmTextSize: Float = 18f,
        var confirmTextColor: Int = ColorUtils.getColor(R.color.ui_theme_c0),
        var confirmTextBold: Boolean = true,

        @LayoutRes
        var customView: Int = 0,

        var onCustomViewInitListener: (View) -> Unit = { },
        var onConfirmClickListener: (View) -> Unit = { },
        var onCancelClickListener: (View) -> Unit = { },

        ) : Parcelable {


        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readFloat(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readFloat(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readFloat(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readFloat(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readFloat(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt(),
        )

        /**
         * 全局无标题时，Message 样式
         */
        fun applyNoTitleStyle() {
            messageBold = false
            messageColor = ColorUtils.getColor(R.color.ui_text_c1)
            messageSize = 16f
            messagePaddingVertical = 10.dpi
        }

        /**
         * 构建
         */
        open fun create() = Companion.create(this)

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(width)
            parcel.writeInt(radius)
            parcel.writeInt(background)
            parcel.writeFloat(dimAmount)
            parcel.writeByte(if (cancelCancelable) 1 else 0)
            parcel.writeByte(if (confirmCancelable) 1 else 0)
            parcel.writeByte(if (touchOutsideCancelable) 1 else 0)
            parcel.writeByte(if (backCancelable) 1 else 0)
            parcel.writeString(title)
            parcel.writeFloat(titleSize)
            parcel.writeByte(if (titleBold) 1 else 0)
            parcel.writeInt(titleColor)
            parcel.writeString(message)
            parcel.writeFloat(messageSize)
            parcel.writeByte(if (messageBold) 1 else 0)
            parcel.writeInt(messagePaddingVertical)
            parcel.writeInt(messageColor)
            parcel.writeString(cancelText)
            parcel.writeFloat(cancelTextSize)
            parcel.writeInt(cancelTextColor)
            parcel.writeByte(if (cancelTextBold) 1 else 0)
            parcel.writeString(confirmText)
            parcel.writeFloat(confirmTextSize)
            parcel.writeInt(confirmTextColor)
            parcel.writeByte(if (confirmTextBold) 1 else 0)
            parcel.writeInt(customView)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Builder> {
            override fun createFromParcel(parcel: Parcel): Builder {
                return Builder(parcel)
            }

            override fun newArray(size: Int): Array<Builder?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object {
        const val ARG_BUILDER = "ARG_BUILDER"

        @JvmStatic
        fun create(builder: Builder): UiNormalDialog {
            return UiNormalDialog().apply {
                arguments = Bundle().also {
                    it.putParcelable(ARG_BUILDER, builder)
                }
            }
        }
    }
}