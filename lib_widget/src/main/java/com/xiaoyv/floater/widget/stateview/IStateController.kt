package com.xiaoyv.floater.widget.stateview

import android.view.View
import androidx.annotation.DrawableRes

/**
 * IStateController
 *
 * @author why
 * @since 2021/10/15
 */
interface IStateController {
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
}