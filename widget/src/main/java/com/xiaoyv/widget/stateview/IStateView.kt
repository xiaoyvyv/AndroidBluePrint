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
    fun showEmptyView(): View?

    fun showTipView(msg: String?): View?
    fun showTipView(msg: String?, @DrawableRes imgResId: Int?): View?

    fun showLoadingView(): View?

    fun showRetryView(): View?
    fun showRetryView(msg: String?): View?
    fun showRetryView(msg: String?, btText: String?): View?
    fun showRetryView(msg: String?, btText: String?, @DrawableRes imgResId: Int?): View?

    fun onGetOrCreateStateView(): StateView
}