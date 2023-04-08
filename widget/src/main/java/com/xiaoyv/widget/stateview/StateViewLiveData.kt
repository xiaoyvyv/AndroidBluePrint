@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.widget.stateview

import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.StringUtils

/**
 * StateViewLiveData
 *
 * @author why
 * @since 2023/3/11
 */
open class StateViewLiveData : MutableLiveData<StateViewLiveData.MutableState>() {
    private val defaultHideState = MutableState(type = StateType.STATE_HIDE)
    private val defaultLoadingState = MutableState(type = StateType.STATE_LOADING)

    fun showContent() {
        value = defaultHideState
    }

    fun showLoading() {
        value = defaultLoadingState
    }

    fun showTips(tipMsgResId: Int, tipImage: Int) {
        showTips(StringUtils.getString(tipMsgResId), tipImage)
    }

    fun showTips(tipMsg: String, tipImage: Int) {
        value = MutableState(StateType.STATE_TIPS, tipMsg, tipImage)
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