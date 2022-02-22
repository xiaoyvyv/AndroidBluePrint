package com.xiaoyv.widget.utils

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import com.blankj.utilcode.util.Utils
import com.xiaoyv.widget.span.CustomTypefaceSpan
import me.jessyan.autosize.utils.AutoSizeUtils


/**
 * UiUtils
 *
 * @author why
 * @since 2021/12/15
 */
object UiUtils {

    @JvmStatic
    fun getAttrColor(@AttrRes attrRes: Int): Int {
        val typedValue = TypedValue()
        Utils.getApp().theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue.data
    }


    @JvmStatic
    fun getClickSpan(
        text: String,
        @ColorInt color: Int,
        onClickAgreement: ClickableSpan,
    ): SpannableStringBuilder {
        return SpannableStringBuilder(text).also {
            it.setSpan(
                ForegroundColorSpan(color), 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            it.setSpan(
                onClickAgreement, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }


    @JvmStatic
    fun getTextSpan(
        text: String,
        @ColorInt color: Int,
        bold: Boolean = false,
        textSize: Float = 14f,
        @FontRes textFont: Int = 0,
        startIndex: Int = 0,
        endIndex: Int = text.length,
    ): SpannableStringBuilder {
        return SpannableStringBuilder(text).also {
            val fontSize = AutoSizeUtils.sp2px(Utils.getApp(), textSize)

            it.setSpan(
                ForegroundColorSpan(color), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            it.setSpan(
                StyleSpan(if (bold) Typeface.BOLD else Typeface.NORMAL),
                startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            it.setSpan(
                AbsoluteSizeSpan(fontSize, false),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (textFont != 0) {
                val font = ResourcesCompat.getFont(Utils.getApp(), textFont)
                it.setSpan(
                    CustomTypefaceSpan(font ?: return@also),
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }
}


/**
 * 由于自定义 View 中的一些简化拓展方法
 */
fun View.getDpx(dp: Float): Int = if (isInEditMode) (dp * 2.5).toInt() else dp.dpi

fun View.getSpx(sp: Float): Int = if (isInEditMode) (sp * 2.5).toInt() else sp.spi

@ColorInt
fun View.getColor(@ColorRes colorResId: Int): Int =
    ResourcesCompat.getColor(resources, colorResId, null)

fun View.getDrawable(@DrawableRes drawableResId: Int): Drawable? =
    ResourcesCompat.getDrawable(resources, drawableResId, null)

fun View.getString(@StringRes stringResId: Int): String = resources.getString(stringResId)

fun ImageView.imageTintColorRes(@ColorRes colorResId: Int) {
    imageTintList = ColorStateList.valueOf(getColor(colorResId))
}

fun ImageView.setImageTintColorInt(@ColorInt color: Int) {
    imageTintList = ColorStateList.valueOf(color)
}


