package com.xiaoyv.blueprint.base

import androidx.annotation.IntDef
import com.github.nukc.stateview.StateView


/**
 * BaseView
 *
 * @author why
 * @since 2020/11/28
 */
interface IBaseView {

    /**
     * 提示信息
     */
    fun p2vShowToast(msg: String? = null)
    fun p2vShowSnack(msg: String? = null, @SnackBarType snackBarType: Int = SNACK_BAT_TYPE_NORMAL)

    /**
     * 加载框
     */
    fun p2vShowLoading(msg: String? = null)
    fun p2vHideLoading()

    fun p2vShowNormalView()
    fun p2vShowEmptyView()
    fun p2vShowTipView(msg: String?)
    fun p2vShowLoadingView()
    fun p2vShowRetryView()
    fun p2vShowRetryView(msg: String?)
    fun p2vShowRetryView(msg: String?, btText: String?)
    fun vGetStateView(): StateView

    fun vRetryClick()

    @IntDef(
        SNACK_BAT_TYPE_NORMAL,
        SNACK_BAT_TYPE_SUCCESS,
        SNACK_BAT_TYPE_WARNING,
        SNACK_BAT_TYPE_ERROR
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class SnackBarType

    companion object {
        const val SNACK_BAT_TYPE_NORMAL = 0x11
        const val SNACK_BAT_TYPE_SUCCESS = 0x12
        const val SNACK_BAT_TYPE_WARNING = 0x13
        const val SNACK_BAT_TYPE_ERROR = 0x14
    }
}