@file:Suppress("MemberVisibilityCanBePrivate", "HasPlatformType")

package com.xiaoyv.blueprint.base.mvvm.normal

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.StringUtils
import com.xiaoyv.blueprint.base.GlobalStringResLoading
import com.xiaoyv.blueprint.entity.LoadingState
import com.xiaoyv.widget.kts.sendValue
import com.xiaoyv.widget.stateview.StateViewLiveData
import java.lang.ref.WeakReference


/**
 * BaseViewModel
 *
 * @author why
 * @since 2022/7/9
 **/
open class BaseViewModel : ViewModel() {
    var reference: WeakReference<Context>? = null

    val context: Context
        get() = reference?.get()
            ?: throw NullPointerException("viewModel context 已经解绑，请检查泄露代码！！！")

    /**
     * 加载状态 对话框形式
     */
    internal var loadingDialogCancelable = true
    internal var loadingDialogTips = StringUtils.getString(GlobalStringResLoading)

    val loadingDialogLiveData = MutableLiveData<LoadingState>()

    /**
     * 加载状态 View 形式
     */
    val loadingViewState = StateViewLiveData()

    fun onAttach(context: Context) {
        reference?.clear()
        reference = null
        reference = WeakReference(context)
    }

    fun onDetach() {
        reference?.clear()
        reference = null
    }

    /**
     * 更新 Loading tip
     */
    fun resetLoadingTip(loadingTip: String? = null) {
        loadingDialogTips =
            loadingTip.orEmpty().ifBlank { StringUtils.getString(GlobalStringResLoading) }
    }

    /**
     * 获取加载中对话框信息的绑定
     */
    @JvmOverloads
    fun loadingDialogState(
        loadingTip: String? = null,
        cancelable: Boolean = true
    ): MutableLiveData<LoadingState> {
        loadingDialogCancelable = cancelable
        resetLoadingTip(loadingTip)
        return loadingDialogLiveData
    }

    /**
     * 发送 Loading 事件
     */
    fun sendLoadingDialogState(state: LoadingState) {
        loadingDialogLiveData.sendValue(state)
    }

    open fun onViewCreated() {}

    open fun onViewDestroy() {}

    open fun onDestroy() {}

}