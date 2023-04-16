package com.xiaoyv.blueprint.base

import android.view.View
import androidx.annotation.IntDef
import com.github.nukc.stateview.StateView
import com.xiaoyv.floater.widget.stateview.IStateController


/**
 * BaseView
 *
 * @author why
 * @since 2020/11/28
 */
interface IBaseView {
    val stateController: IStateController

    /**
     * 提示信息
     */
    fun showToast(msg: String? = null)
    fun showSnack(msg: String? = null, @SnackBarType snackBarType: Int = SNACK_BAT_TYPE_NORMAL)

    /**
     * 加载框
     */
    fun showLoading(msg: String? = null)
    fun hideLoading()

    /**
     * 状态布局按钮点击
     */
    fun onClickStateView(stateView: StateView, view: View)
    fun onCreateStateController(): IStateController?

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