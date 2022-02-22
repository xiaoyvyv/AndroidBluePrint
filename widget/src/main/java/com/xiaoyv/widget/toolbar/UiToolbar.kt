@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.xiaoyv.widget.toolbar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ScreenUtils
import com.xiaoyv.widget.R
import com.xiaoyv.widget.databinding.UiViewToolbarBinding
import com.xiaoyv.widget.utils.dpi
import com.xiaoyv.widget.utils.getActivity

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
            val topPadding = if (value) BarUtils.getStatusBarHeight() else 0
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
        }
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
     * 左边按钮、点击事件
     */
    @JvmOverloads
    fun setLeftIcon(
        @DrawableRes vararg iconRes: Int,
        onBarClickListener: OnBarClickListener? = defaultClick4Finish
    ) {
        binding.llLeft.removeAllViews()
        iconRes.forEachIndexed { index, icon ->
            binding.llLeft.addView(AppCompatImageView(context).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(44.dpi, 44.dpi)
                setPadding(12.dpi, 11.dpi, 10.dpi, 11.dpi)
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
        onBarClickListener: OnBarClickListener? = null
    ) {
        binding.llRight.removeAllViews()
        iconRes.forEachIndexed { index, icon ->
            binding.llRight.addView(AppCompatImageView(context).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(44.dpi, 44.dpi)
                setPadding(10.dpi, 11.dpi, 12.dpi, 11.dpi)
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
}