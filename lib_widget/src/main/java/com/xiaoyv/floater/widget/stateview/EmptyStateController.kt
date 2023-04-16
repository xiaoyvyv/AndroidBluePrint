package com.xiaoyv.floater.widget.stateview

import android.view.View

/**
 * EmptyStateController
 *
 * @author why
 * @since 2023/4/9
 */
class EmptyStateController : IStateController {
    override fun showNormalView() {

    }

    override fun showLoadingView(): View? {
        return null
    }

    override fun showEmptyView(): View? {
        return null
    }

    override fun showTipView(msg: String?, imgResId: Int?): View? {
        return null
    }

    override fun showRetryView(msg: String?, btText: String?, imgResId: Int?): View? {
        return null
    }
}