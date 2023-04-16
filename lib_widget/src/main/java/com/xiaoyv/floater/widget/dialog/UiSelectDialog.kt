@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.xiaoyv.floater.widget.dialog

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xiaoyv.floater.widget.R
import com.xiaoyv.floater.widget.databinding.UiDialogSelectBinding
import com.xiaoyv.floater.widget.databinding.UiDialogSelectItemBinding
import com.xiaoyv.floater.widget.kts.canShowInActivity
import com.xiaoyv.floater.widget.kts.canShowInFragment
import com.xiaoyv.floater.widget.kts.dpi

/**
 * UiSelectDialog
 *
 * @author why
 * @since 2022/2/7
 */
class UiSelectDialog : DialogFragment() {
    private var fragmentTag = javaClass.simpleName
    private var builder: Builder? = null

    private val offset = 2

    /**
     * 当前选中的位置
     */
    private var selectPosition: Int = offset

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        builder = arguments?.getParcelable(ARG_BUILDER) as? Builder ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = UiDialogSelectBinding.inflate(layoutInflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = UiDialogSelectBinding.bind(view)
        val param = builder ?: return

        isCancelable = param.backCancelable
        selectPosition = param.defaultIndex + offset

        // 背景圆角
        binding.root.setCardBackgroundColor(param.background)
        binding.root.radius = param.radius.toFloat()

        // 视图相关
        binding.tvTitle.isGone = param.title.isNullOrBlank()
        binding.tvCancel.isGone = param.cancelText.isNullOrBlank()
        binding.tvConfirm.isGone = param.confirmText.isNullOrBlank()
        binding.vDivider.isGone = binding.tvCancel.isGone && binding.tvConfirm.isGone

        // 标题
        binding.tvTitle.text = param.title
        binding.tvTitle.textSize = param.titleSize
        binding.tvTitle.setTextColor(param.titleColor)
        binding.tvTitle.typeface =
            if (param.titleBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT


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


        val baseBinderAdapter = BaseBinderAdapter()
        val optionBinder = OptionBinder()
        baseBinderAdapter.addItemBinder(optionBinder)
        binding.rvOptions.adapter = baseBinderAdapter
        binding.rvOptions.hasFixedSize()

        // 滑动约束
        val linearSnapHelper = LinearSnapHelper()
        linearSnapHelper.attachToRecyclerView(binding.rvOptions)

        // 上下两端添加空白占位
        baseBinderAdapter.setList(convertOptionItems(param.options))

        // 点击 取消键
        binding.tvCancel.setOnClickListener {
            if (param.cancelCancelable) {
                dismiss()
            }
            param.cancelClickListener.invoke(it)
        }
        // 点击 确定键
        binding.tvConfirm.setOnClickListener {
            // 首尾加的占位不允许选择
            if (selectPosition < offset || selectPosition > baseBinderAdapter.data.size - (offset + 1)) {
                return@setOnClickListener
            }

            if (param.confirmCancelable) {
                dismiss()
            }

            // 获取选中的文案
            val selectItem = baseBinderAdapter.getItemOrNull(selectPosition)
            if (selectItem != null) {
                param.confirmClickListener.invoke(it, selectItem.toString())
            }
        }

        val linearLayoutManager = binding.rvOptions.layoutManager as? LinearLayoutManager ?: return

        binding.rvOptions.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                selectPosition = firstItemPosition + offset
                binding.rvOptions.post {
                    baseBinderAdapter.notifyDataSetChanged()
                }
            }
        })

        // 滑动到默认位置
        linearLayoutManager.scrollToPositionWithOffset(selectPosition, 42.dpi * offset)
    }

    /**
     * 上下两端添加空白占位
     */
    private fun convertOptionItems(options: List<String>): List<String> {
        if (options.isEmpty()) {
            return emptyList()
        }
        return mutableListOf<String>().apply {
            add(StringUtils.getString(R.string.ui_common_empty))
            add(StringUtils.getString(R.string.ui_common_empty))
            addAll(options)
            add(StringUtils.getString(R.string.ui_common_empty))
            add(StringUtils.getString(R.string.ui_common_empty))
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
            gravity = Gravity.BOTTOM
        }
    }

    fun show(fragmentActivity: FragmentActivity) {
        if (canShowInActivity(fragmentActivity)) {
            showNow(fragmentActivity.supportFragmentManager, fragmentTag)
        }
    }

    fun show(fragment: Fragment) {
        if (canShowInFragment(fragment)) {
            showNow(fragment.childFragmentManager, fragmentTag)
        }
    }

    override fun dismiss() {
        if (isAdded) {
            super.dismissAllowingStateLoss()
        }
    }


    /**
     * 对话框选项
     */
    inner class OptionBinder : BaseItemBinder<String, BaseViewHolder>() {
        override fun convert(holder: BaseViewHolder, data: String) {
            val binding = UiDialogSelectItemBinding.bind(holder.itemView)
            binding.tvOption.text = data

            // 选中加粗
            if (selectPosition == holder.bindingAdapterPosition) {
                binding.tvOption.typeface = Typeface.DEFAULT_BOLD
            } else {
                binding.tvOption.typeface = Typeface.DEFAULT
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BaseViewHolder(
            UiDialogSelectItemBinding.inflate(LayoutInflater.from(context), parent, false).root
        )

    }


    /**
     * 对话框建造者
     */
    open class Builder(
        var width: Int = ScreenUtils.getScreenWidth(),
        var radius: Int = 0,
        var background: Int = ColorUtils.getColor(R.color.ui_system_background),
        var dimAmount: Float = 0.38f,
        var cancelCancelable: Boolean = true,
        var confirmCancelable: Boolean = true,
        var touchOutsideCancelable: Boolean = false,
        var backCancelable: Boolean = true,

        var title: String? = null,
        var titleSize: Float = 16f,
        var titleBold: Boolean = true,
        var titleColor: Int = ColorUtils.getColor(R.color.ui_text_c1),

        var cancelText: String? = StringUtils.getString(R.string.ui_common_cancel),
        var cancelTextSize: Float = 16f,
        var cancelTextColor: Int = ColorUtils.getColor(R.color.ui_text_c1),
        var cancelTextBold: Boolean = false,

        var confirmText: String? = StringUtils.getString(R.string.ui_common_done),
        var confirmTextSize: Float = 16f,
        var confirmTextColor: Int = ColorUtils.getColor(R.color.ui_theme_c0),
        var confirmTextBold: Boolean = true,

        var options: List<String> = arrayListOf(),
        var defaultIndex: Int = 0,

        var confirmClickListener: (View, String) -> Unit = { _, _ -> },
        var cancelClickListener: (View) -> Unit = { },
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
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readFloat(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.createStringArrayList().orEmpty(),
            parcel.readInt()
        )


        /**
         * 构建
         */
        fun create() = create(this)

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
            parcel.writeString(cancelText)
            parcel.writeFloat(cancelTextSize)
            parcel.writeInt(cancelTextColor)
            parcel.writeByte(if (cancelTextBold) 1 else 0)
            parcel.writeString(confirmText)
            parcel.writeFloat(confirmTextSize)
            parcel.writeInt(confirmTextColor)
            parcel.writeByte(if (confirmTextBold) 1 else 0)
            parcel.writeStringList(options)
            parcel.writeInt(defaultIndex)
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
        private const val ARG_BUILDER = "ARG_BUILDER"

        @JvmStatic
        fun create(builder: Builder): UiSelectDialog {
            return UiSelectDialog().apply {
                arguments = Bundle().also {
                    it.putParcelable(ARG_BUILDER, builder)
                }
            }
        }
    }

}