package com.xiaoyv.widget.dialog

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ColorUtils
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.binder.BaseItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xiaoyv.widget.R
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.databinding.UiDialogNormalBinding
import com.xiaoyv.widget.utils.dpi
import com.xiaoyv.widget.utils.getAttrDrawable

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
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            overScrollMode = View.OVER_SCROLL_NEVER
            adapter = optionsAdapter
            hasFixedSize()
        }

        binding.flView.removeAllViews()
        binding.flView.addView(
            recyclerView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }

    /**
     * 对话框选项
     */
    inner class OptionBinder : BaseItemBinder<String, BaseViewHolder>() {

        override fun convert(holder: BaseViewHolder, data: String) {
            val itemView = holder.itemView as AppCompatTextView
            itemView.text = data
            val position = holder.bindingAdapterPosition

            // 点击回调
            itemView.setOnFastLimitClickListener {
                val invoke = optionsBuilder.optionsClickListener.invoke(
                    optionsBuilder.itemDataList[position], position
                )
                if (invoke) {
                    dismissAllowingStateLoss()
                }
            }

            // 最后一条
            if (position == adapter.itemCount - 1 && optionsBuilder.lastItemColor != 0) {
                itemView.setTextColor(optionsBuilder.lastItemColor)
            } else {
                itemView.setTextColor(optionsBuilder.itemTextColor)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BaseViewHolder(
            AppCompatTextView(context).apply {
                layoutParams = RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    optionsBuilder.itemHeight
                )
                textSize = optionsBuilder.itemTextSize
                isClickable = true
                isFocusable = true
                background = context.getAttrDrawable(android.R.attr.selectableItemBackground)
                setTextColor(optionsBuilder.itemTextColor)
            }
        )
    }

    class Builder(
        var itemHeight: Int = 44.dpi,
        var itemDataList: List<String> = arrayListOf(),

        var itemTextSize: Float = 14f,
        var itemTextColor: Int = ColorUtils.getColor(R.color.ui_text_c1),

        @ColorInt
        var lastItemColor: Int = 0,
        var optionsClickListener: (String, Int) -> Boolean = { _, _ -> true }
    ) : UiNormalDialog.Builder(), Parcelable{

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