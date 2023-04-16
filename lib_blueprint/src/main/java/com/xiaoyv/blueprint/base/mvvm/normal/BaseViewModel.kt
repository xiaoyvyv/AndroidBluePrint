@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base.mvvm.normal

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.StringUtils
import com.xiaoyv.blueprint.R
import com.xiaoyv.blueprint.entity.LoadingState
import com.xiaoyv.floater.widget.stateview.StateViewLiveData
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
    var loadingDialogTips = StringUtils.getString(R.string.brvah_loading)
    val loadingDialogState = MutableLiveData<LoadingState>()

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

    open fun onViewCreated() {}

    open fun onViewDestroy() {}

    open fun onDestroy() {}
}