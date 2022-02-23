package com.xiaoyv.blueprint.base

import android.view.View
import androidx.annotation.IntDef
import com.github.nukc.stateview.StateView
import com.xiaoyv.widget.stateview.StateViewImpl


/**
 * BaseView
 *
 * @author why
 * @since 2020/11/28
 */
interface IBaseView {
    val stateController: StateViewImpl
        get() = p2vGetStateController()

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

    /**
     * 状态布局按钮点击
     */
    fun p2vClickStatusView(stateView: StateView, view: View)

    fun p2vGetStateController(): StateViewImpl

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