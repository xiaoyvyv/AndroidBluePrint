package com.xiaoyv.widget.dialog

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ColorUtils
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xiaoyv.floater.widget.R
import com.xiaoyv.floater.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.floater.widget.databinding.UiDialogNormalBinding
import com.xiaoyv.floater.widget.databinding.UiDialogOptionsItemBinding
import com.xiaoyv.floater.widget.kts.dpi

/**
 * UiOptionsDialog
 *
 * @author why
 * @since 2022/2/24
 */
class UiOptionsDialog : UiNormalDialog() {

    private lateinit var optionsAdapter: BaseBinderAdapter

    private val optionsBuilder: Builder
        get() = builder as Builder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = UiDialogNormalBinding.bind(view)

        val optionBinder = OptionBinder()

        optionsAdapter = BaseBinderAdapter()
        optionsAdapter.addItemBinder(optionBinder)

        val recyclerView = RecyclerView(requireActivity()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
            )
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            overScrollMode = View.OVER_SCROLL_NEVER
            adapter = optionsAdapter
            hasFixedSize()
        }

        binding.flView.removeAllViews()
        binding.flView.addView(recyclerView)
        binding.flView.isVisible = true

        // 设置数据
        optionsAdapter.setList(optionsBuilder.itemDataList)
    }

    /**
     * 对话框选项
     */
    inner class OptionBinder : BaseItemBinder<String, BaseViewHolder>() {

        override fun convert(holder: BaseViewHolder, data: String) {
            val binding = UiDialogOptionsItemBinding.bind(holder.itemView)
            val position = holder.bindingAdapterPosition

            binding.tvOption.text = data
            binding.vDivider.isVisible = optionsBuilder.itemDivider
            binding.tvOption.textSize = optionsBuilder.itemTextSize

            // 点击回调
            binding.tvOption.setOnFastLimitClickListener {
                optionsBuilder.onOptionsClickListener.invoke(
                    this@UiOptionsDialog, optionsBuilder.itemDataList[position], position
                )
            }

            // 最后一条
            if (position == adapter.itemCount - 1 && optionsBuilder.itemLastColor != 0) {
                binding.tvOption.setTextColor(optionsBuilder.itemLastColor)
            } else {
                binding.tvOption.setTextColor(optionsBuilder.itemTextColor)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BaseViewHolder(
            UiDialogOptionsItemBinding.inflate(LayoutInflater.from(context), parent, false)
                .apply {
                    tvOption.layoutParams = RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        optionsBuilder.itemHeight
                    )
                }.root
        )
    }

    class Builder(
        var itemHeight: Int = 52.dpi,
        var itemDataList: List<String> = arrayListOf(),

        var itemTextSize: Float = 16f,
        var itemTextColor: Int = ColorUtils.getColor(R.color.ui_text_c1),

        var itemDivider: Boolean = true,

        @ColorInt
        var itemLastColor: Int = 0,
        var onOptionsClickListener: (UiOptionsDialog, String, Int) -> Boolean = { dialog, _, _ ->
            dialog.dismissAllowingStateLoss()
            true
        }
    ) : UiNormalDialog.Builder(confirmText = null, cancelText = null), Parcelable {

        /**
         * 构建
         */
        override fun create() = Companion.create(this)
    }


    companion object {

        @JvmStatic
        fun create(builder: Builder): UiOptionsDialog {
            return UiOptionsDialog().apply {
                arguments = Bundle().also {
                    it.putParcelable(ARG_BUILDER, builder)
                }
            }
        }
    }
}