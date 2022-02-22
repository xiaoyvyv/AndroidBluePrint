@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.widget.button

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.blankj.utilcode.util.LogUtils
import com.xiaoyv.widget.R
import com.xiaoyv.widget.databinding.UiViewButtonBinding
import com.xiaoyv.widget.utils.*

/**
 * UiButton 公用按钮
 *
 * @author why
 * @since 2021/12/17
 */
class UiButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {
    private val binding = UiViewButtonBinding.inflate(LayoutInflater.from(context), this)

    private var limitFastClickListener: OnClickListener? = null

    private var lastCallbackMill = 0L

    /**
     * 快速点击限制，默认 500ms
     */
    var fastLimitMill = 500

    /**
     * 正常颜色
     */
    @ColorInt
    var normalColor: Int = getColor(R.color.ui_theme_c0)
        set(value) {
            field = value
            refreshButton()
        }

    /**
     * 按下颜色
     */
    @ColorInt
    var selectColor: Int = getColor(R.color.ui_theme_c4)
        set(value) {
            field = value
            refreshButton()
        }


    /**
     * 文案
     */
    var text: String = "This is a Button"
        set(value) {
            field = value
            binding.tvTitle.text = value
        }

    /**
     * 文案颜色
     */
    @ColorInt
    var textColor: Int = Color.WHITE
        set(value) {
            field = value
            binding.tvTitle.setTextColor(value)
        }

    /**
     * 文案字体大小
     */
    var textSize: Float = getSpx(16f).toFloat()
        set(value) {
            field = value
            binding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }

    /**
     * 文案字体 top bottom padding
     */
    var textVerPadding: Int = getDpx(14f)
        set(value) {
            field = value
            binding.tvTitle.setPadding(0, value, 0, value)
        }


    /**
     * 边框颜色
     */
    @ColorInt
    var borderColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            refreshButton()
        }


    /**
     * 边框宽度
     */
    var borderWidth: Int = getDpx(0.5f)
        set(value) {
            field = value
            refreshButton()
        }


    /**
     * 圆角
     */
    var radius: Float = 0f
        set(value) {
            field = value
            refreshButton()
        }


    /**
     * Logo
     */
    var logo: Drawable? = null
        set(value) {
            field = value
            binding.ivIcon.setImageDrawable(value)
        }

    /**
     * logo 大小
     */
    var logoSize: Int = getDpx(32f)
        set(value) {
            field = value
            binding.ivIcon.updateLayoutParams {
                height = value
                width = value
            }
        }

    /**
     * Logo 靠近文案还是左侧固定边距
     */
    var logoNearText: Boolean = false
        set(value) {
            field = value
            binding.ivIcon.updateLayoutParams<LayoutParams> {
                horizontalBias = if (value) 1f else 0f
            }
        }

    /**
     * 是否加载中
     */
    var isLoading = false
        set(value) {
            field = value
            if (value) {
                setEnable(false, 1f)
                showLoading(value)
            } else {
                setEnable(true)
                showLoading(value)
            }
        }

    override fun setEnabled(enabled: Boolean) = setEnable(enabled)

    /**
     * 设置可点击或禁用 UI
     */
    private fun setEnable(
        enable: Boolean,
        @FloatRange(from = 0.0, to = 1.0) disableAlpha: Float = 0.38f,
    ) {
        super.setEnabled(enable)
        alpha = if (enable) 1f else disableAlpha
    }

    /**
     * 中心加载图标
     */
    var loadingIcon: Drawable? = getDrawable(R.drawable.ui_icon_loading)
        set(value) {
            field = value
            binding.ivLoading.setImageDrawable(value)
        }

    /**
     * 中心加载图标 着色颜色
     */
    @ColorInt
    var loadingIconColor: Int = Color.WHITE
        set(value) {
            field = value
            binding.ivLoading.setImageTintColorInt(value)
        }

    /**
     * 中心加载图标 大小
     */
    var loadingIconSize: Int = getDpx(20f)
        set(value) {
            field = value
            binding.ivLoading.updateLayoutParams {
                height = value
                width = value
            }
        }

    init {
        isClickable = true

        context.obtainStyledAttributes(attrs, R.styleable.UiButton).also {
            normalColor = it.getColor(R.styleable.UiButton_ui_normal_color, normalColor)
            selectColor = it.getColor(R.styleable.UiButton_ui_select_color, selectColor)
            borderColor = it.getColor(R.styleable.UiButton_ui_border_color, borderColor)
            borderWidth =
                it.getDimensionPixelSize(R.styleable.UiButton_ui_border_width, borderWidth)
            radius = it.getDimension(R.styleable.UiButton_ui_radius, radius)
            logo = it.getDrawable(R.styleable.UiButton_ui_logo)
            logoSize = it.getDimensionPixelSize(R.styleable.UiButton_ui_logo_size, logoSize)
            logoNearText = it.getBoolean(R.styleable.UiButton_ui_logo_near_text, false)
            text = it.getString(R.styleable.UiButton_ui_bt_text) ?: text
            textVerPadding =
                it.getDimensionPixelSize(
                    R.styleable.UiButton_ui_bt_text_padding_vertical,
                    textVerPadding
                )
            textSize = it.getDimension(R.styleable.UiButton_ui_bt_text_size, textSize)
            textColor = it.getColor(R.styleable.UiButton_ui_bt_text_color, textColor)
            fastLimitMill = it.getInteger(R.styleable.UiButton_ui_fast_limit, fastLimitMill)
            loadingIcon = it.getDrawable(R.styleable.UiButton_ui_loading) ?: loadingIcon
            loadingIconSize =
                it.getDimensionPixelSize(R.styleable.UiButton_ui_loading_size, loadingIconSize)

            // 中心加载图标颜色默认同文案颜色
            loadingIconColor = it.getColor(R.styleable.UiButton_ui_loading_color, textColor)
        }.recycle()

        refreshButton()

        // 点击事件
        super.setOnClickListener {
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - lastCallbackMill > fastLimitMill) {
                lastCallbackMill = currentTimeMillis
                limitFastClickListener?.onClick(it)
                return@setOnClickListener
            }
            LogUtils.i("快速点击限制毫秒：$fastLimitMill ms")
        }
    }

    /**
     * 是否为加载中 UI
     */
    private fun showLoading(loading: Boolean) = post {
        binding.tvTitle.text = if (loading) null else text
        binding.ivLoading.isVisible = loading
        if (loading) {
            binding.ivLoading.animation = RotateAnimation(
                0f, 360f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 1500
                repeatCount = -1
                repeatMode = Animation.RESTART
                interpolator = LinearInterpolator()
                start()
            }
        } else {
            binding.ivLoading.clearAnimation()
        }
    }

    private fun refreshButton() {
        binding.root.background = DrawableUtils.createSelectorDrawable(
            normal = DrawableUtils.createStrokeDrawable(
                borderColor,
                normalColor,
                borderWidth,
                radius
            ),
            pressed = DrawableUtils.createStrokeDrawable(
                borderColor,
                selectColor,
                borderWidth,
                radius
            )
        )
    }


    override fun setOnClickListener(l: OnClickListener?) {
        limitFastClickListener = l
    }
}