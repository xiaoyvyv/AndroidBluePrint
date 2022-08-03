@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base.mvvm.nav

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xiaoyv.blueprint.utils.ioCoroutineContext
import com.xiaoyv.blueprint.utils.mainCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


/**
 * BaseNavViewModel
 *
 * @author why
 * @since 2022/7/9
 **/
open class BaseNavViewModel : ViewModel() {
    var reference: WeakReference<Context>? = null

    val context: Context
        get() = reference?.get() ?: throw NullPointerException("viewModel context 已经解绑，请检查泄露代码！！！")

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

    /**
     * UI 线程
     */
    fun launchUi(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(mainCoroutineContext) {
            block(this)
        }
    }

    /**
     * IO 线程
     */
    fun launchIo(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(ioCoroutineContext) {
            block(this)
        }
    }

}