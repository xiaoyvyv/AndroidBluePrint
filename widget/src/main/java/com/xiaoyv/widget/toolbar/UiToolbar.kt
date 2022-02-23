@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.xiaoyv.widget.toolbar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.Utils
import com.xiaoyv.widget.R
import com.xiaoyv.widget.databinding.UiViewToolbarBinding
import com.xiaoyv.widget.utils.*

/**
 * UiToolbar
 *
 * @author why
 * @since 2021/12/17
 */
class UiToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    private val binding = UiViewToolbarBinding.inflate(LayoutInflater.from(context), this)

    /**
     * 是否适配状态栏高度
     */
    var fitStatusBar: Boolean = true
        set(value) {
            field = value
            val topPadding = if (value) getStatusBarHeight() else 0
            setPadding(0, topPadding, 0, 0)
        }

    /**
     * 标题文字左右内边距
     */
    var titleHorizontalPadding: Int = 0
        set(value) {
            field = value
            binding.tvToolbarTitle.setPadding(value, 0, value, 0)
        }

    /**
     * 标题
     */
    var title: String? = ""
        set(value) {
            field = value
            binding.tvToolbarTitle.text = value
        }

    /**
     * 是否显示底部分割线
     */
    var bottomDivider: Boolean = true
        set(value) {
            field = value
            binding.vDivider.isVisible = value
        }

    /**
     * 文案颜色
     */
    @ColorInt
    var titleColor: Int = if (isInEditMode) Color.BLACK else ColorUtils.getColor(R.color.ui_text_c1)
        set(value) {
            field = value
            binding.tvToolbarTitle.setTextColor(value)
        }

    /**
     * 点击默认结束当前页面
     */
    private val defaultClick4Finish: OnBarClickListener = object : OnBarClickListener {
        override fun onClick(view: View, which: Int) {
            val activity = getActivity()
            if (activity != null) {
                activity.onBackPressed()
            } else {
                ActivityUtils.getTopActivity()?.onBackPressed()
            }
        }
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.UiToolbar).also {
            fitStatusBar = it.getBoolean(R.styleable.UiToolbar_ui_fit_status_bar, true)
            bottomDivider = it.getBoolean(R.styleable.UiToolbar_ui_bottom_divider, true)
            title = it.getString(R.styleable.UiToolbar_ui_title) ?: "标题栏"

            titleHorizontalPadding = it.getDimension(
                R.styleable.UiToolbar_ui_title_horizontal_padding,
                if (isInEditMode) 60f else 44.dpi.toFloat()
            ).toInt()
        }.recycle()

        // 默认左侧返回按钮实现
        if (!isInEditMode) {
            setLeftIcon(R.drawable.ui_icon_nav_back, onBarClickListener = defaultClick4Finish)

            // 更新标题栏高度
            binding.tvToolbarTitle.updateLayoutParams {
                height = toolbarHeight
            }
        }
    }


    /**
     * 左边按钮、点击事件
     */
    @JvmOverloads
    fun setLeftIcon(
        @DrawableRes vararg iconRes: Int,
        selectableItemBackground: Boolean = true,
        backgroundDrawable: Drawable? = null,
        sizePx: Int = toolbarHeight,
        paddingPx: Int = (toolbarHeight * 0.27f).toInt(),
        onBarClickListener: OnBarClickListener? = defaultClick4Finish
    ) {
        binding.llLeftContainer.removeAllViews()
        iconRes.forEachIndexed { index, icon ->
            binding.llLeftContainer.addView(AppCompatImageView(context).apply {
                if (selectableItemBackground) {
                    background =
                        context.getAttrDrawable(androidx.appcompat.R.attr.selectableItemBackground)
                    isFocusable = true
                    isClickable = true
                } else {
                    background = backgroundDrawable
                }
                layoutParams = LinearLayoutCompat.LayoutParams(sizePx, sizePx)
                setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                setImageResource(icon)
                setOnClickListener { onBarClickListener?.onClick(it, index) }
            })
        }
    }

    /**
     * 右边按钮、点击事件
     */
    @JvmOverloads
    fun setRightIcon(
        @DrawableRes vararg iconRes: Int,
        selectableItemBackground: Boolean = true,
        backgroundDrawable: Drawable? = null,
        sizePx: Int = toolbarHeight,
        paddingPx: Int = 12.dpi,
        onBarClickListener: OnBarClickListener? = null
    ) {
        binding.llRightContainer.removeAllViews()
        iconRes.forEachIndexed { index, icon ->
            binding.llRightContainer.addView(AppCompatImageView(context).apply {
                if (selectableItemBackground) {
                    background =
                        context.getAttrDrawable(androidx.appcompat.R.attr.selectableItemBackground)
                    isFocusable = true
                    isClickable = true
                } else {
                    background = backgroundDrawable
                }
                layoutParams = LinearLayoutCompat.LayoutParams(sizePx, sizePx)
                setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                setImageResource(icon)
                setOnClickListener { onBarClickListener?.onClick(it, index) }
            })
        }
    }

    /**
     * 点击
     */
    interface OnBarClickListener {

        /**
         * @param view View
         * @param which 位置
         */
        fun onClick(view: View, which: Int)
    }

    companion object {

        /**
         * 内容高度
         */
        val toolbarHeight: Int
            get() {
                var toolbarHeight = Utils.getApp().getAttrDimensionPixelSize(R.attr.uiToolbarHeight)
                if (toolbarHeight == 0) {
                    toolbarHeight =
                        Utils.getApp().resources.getDimensionPixelSize(R.dimen.ui_toolbar_height)
                }
                return toolbarHeight
            }
    }
}