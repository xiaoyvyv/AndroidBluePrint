package com.xiaoyv.widget.stateview

import android.view.View
import androidx.annotation.DrawableRes
import com.github.nukc.stateview.StateView

/**
 * StateView
 *
 * @author why
 * @since 2021/10/15
 */
interface IStateView {
    fun showNormalView()

    fun showLoadingView(): View?

    fun showEmptyView(): View?

    fun showTipView(
        msg: String? = null,
        @DrawableRes imgResId: Int? = null
    ): View?

    fun showRetryView(
        msg: String? = null,
        btText: String? = null,
        @DrawableRes imgResId: Int? = null
    ): View?

    fun onCreateStateView(): StateView
}