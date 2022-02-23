package com.xiaoyv.widget.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.Utils
import com.github.nukc.stateview.StateView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xiaoyv.widget.span.CustomTypefaceSpan
import com.xiaoyv.widget.toolbar.UiToolbar
import me.everything.android.ui.overscroll.IOverScrollDecor
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
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

/**
 * 设置透明背景 BottomSheetDialogFragment
 *
 * @param skipCollapsed  是否跳过折叠
 * @param dimAmount      对话框外部阴影比重(0f ~ 1f)
 * @param dialogBehavior 其他交互控制
 */
fun BottomSheetDialogFragment.onStartTransparentDialog(
    skipCollapsed: Boolean = true,
    @FloatRange(from = 0.0, to = 1.0) dimAmount: Float = 0.25f,
    dialogBehavior: BottomSheetBehavior<FrameLayout>.() -> Unit = {}
) {
    val dialog = dialog as? BottomSheetDialog ?: return
    val window = dialog.window ?: return
    val bottomSheet =
        window.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

    window.setDimAmount(dimAmount)
    window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    dialogBehavior(dialog.behavior)
    dialog.behavior.skipCollapsed = skipCollapsed

    // 设置透明背景，光设置 setBackgroundColor 还不够，高版本的库默认加入了 backgroundTintList 的属性
    // 所以还要修改背景色的着色为透明才行
    bottomSheet?.backgroundTintMode = PorterDuff.Mode.CLEAR
    bottomSheet?.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
    bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
}

/**
 * 越界动画
 */
fun RecyclerView.overScrollV(): IOverScrollDecor =
    OverScrollDecoratorHelper.setUpOverScroll(this, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)

fun RecyclerView.overScrollH(): IOverScrollDecor = OverScrollDecoratorHelper.setUpOverScroll(
    this,
    OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL
)

fun NestedScrollView.overScrollV(): IOverScrollDecor =
    OverScrollDecoratorHelper.setUpStaticOverScroll(
        this,
        OverScrollDecoratorHelper.ORIENTATION_VERTICAL
    )

fun NestedScrollView.overScrollH(): IOverScrollDecor =
    OverScrollDecoratorHelper.setUpStaticOverScroll(
        this,
        OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL
    )

@JvmOverloads
fun StateView.doDelayLoadingAndRun(
    delayMill: Long = 200L,
    action: () -> Unit = {}
) {
    this.showLoading()
    this.postDelayed({
        val activity = getActivity() ?: return@postDelayed
        if (activity.isDestroyed || activity.isFinishing) {
            return@postDelayed
        }
        action.invoke()
    }, delayMill)
}

/**
 * UiToolbar 菜单按钮点击事件
 */
inline fun doOnBarClick(
    crossinline onBarClick: (view: View, which: Int) -> Unit = { _, _ -> }
): UiToolbar.OnBarClickListener {
    return object : UiToolbar.OnBarClickListener {
        override fun onClick(view: View, which: Int) {
            onBarClick.invoke(view, which)
        }
    }
}