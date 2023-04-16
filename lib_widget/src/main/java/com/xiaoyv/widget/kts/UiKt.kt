@file:Suppress("unused")

package com.xiaoyv.widget.kts

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.FloatRange
import com.github.nukc.stateview.StateView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xiaoyv.floater.widget.toolbar.UiToolbar

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


@JvmOverloads
fun StateView.doDelayLoadingAndRun(
    delayMill: Long = 200L,
    action: () -> Unit = {}
) {
    this.showLoading()
    this.postDelayed({
        val activity = fetchActivity() ?: return@postDelayed
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

