@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.floater.widget.stateview

import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.StringUtils
import com.xiaoyv.floater.widget.kts.sendValue

/**
 * StateViewLiveData
 *
 * @author why
 * @since 2023/3/11
 */
open class StateViewLiveData : MutableLiveData<StateViewLiveData.MutableState>() {
    protected val defaultHideState = MutableState(type = StateType.STATE_HIDE)
    protected val defaultLoadingState = MutableState(type = StateType.STATE_LOADING)

    /**
     * 在协程中配套使用时，最后完成时是否自动隐藏状态，如果携程 block 自己处理了状态需要关闭该标准，避免覆盖
     */
    var showContentWhenJobDone = true

    open fun showContent() {
        sendValue(defaultHideState)
    }

    open fun showLoading() {
        sendValue(defaultLoadingState)
    }

    @JvmOverloads
    open fun showTips(tipMsgResId: Int, tipImage: Int = 0) {
        showTips(StringUtils.getString(tipMsgResId), tipImage)
    }

    @JvmOverloads
    open fun showTips(tipMsg: String, tipImage: Int = 0) {
        sendValue(MutableState(StateType.STATE_TIPS, tipMsg, tipImage))
    }

    data class MutableState(
        @StateType
        var type: Int = StateType.STATE_HIDE,
        var tipMsg: String = "",
        @DrawableRes
        var tipImage: Int = 0
    )

    @IntDef(StateType.STATE_HIDE, StateType.STATE_LOADING, StateType.STATE_TIPS)
    @Retention(AnnotationRetention.SOURCE)
    annotation class StateType {
        companion object {
            const val STATE_HIDE = 1
            const val STATE_LOADING = 2
            const val STATE_TIPS = 3
        }
    }
}