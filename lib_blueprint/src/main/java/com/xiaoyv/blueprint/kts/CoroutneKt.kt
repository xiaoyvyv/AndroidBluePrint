package com.xiaoyv.blueprint.kts

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.xiaoyv.blueprint.entity.LoadingState
import com.xiaoyv.blueprint.entity.stateOfEnding
import com.xiaoyv.blueprint.entity.stateOfStarting
import com.xiaoyv.widget.kts.ProcessLifecycleScope
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.sendValue
import com.xiaoyv.widget.stateview.StateViewLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * IO 线程
 */
fun LifecycleOwner.launchIO(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    state: MutableLiveData<LoadingState>? = null,
    stateView: StateViewLiveData? = null,
    error: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launchCatch(Dispatchers.IO, start, state, stateView, error, block)
}

/**
 * UI 线程
 */
fun LifecycleOwner.launchUI(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    state: MutableLiveData<LoadingState>? = null,
    stateView: StateViewLiveData? = null,
    error: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launchCatch(
        Dispatchers.Main.immediate,
        start,
        state,
        stateView,
        error,
        block
    )
}

/**
 * IO 线程
 */
fun ViewModel.launchIO(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    error: (Throwable) -> Unit = {},
    state: MutableLiveData<LoadingState>? = null,
    stateView: StateViewLiveData? = null,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return viewModelScope.launchCatch(Dispatchers.IO, start, state, stateView, error, block)
}

/**
 * UI 线程
 */
fun ViewModel.launchUI(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    state: MutableLiveData<LoadingState>? = null,
    stateView: StateViewLiveData? = null,
    error: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
): Job {
    return viewModelScope.launchCatch(
        Dispatchers.Main.immediate,
        start,
        state,
        stateView,
        error,
        block
    )
}

/**
 * 全局进程
 */
fun launchProcess(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    state: MutableLiveData<LoadingState>? = null,
    stateView: StateViewLiveData? = null,
    error: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
): Job {
    return ProcessLifecycleScope.launchCatch(context, start, state, stateView, error, block)
}

/**
 * CoroutineScope.launchCatch()
 */
fun CoroutineScope.launchCatch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    state: MutableLiveData<LoadingState>? = null,
    stateView: StateViewLiveData? = null,
    error: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
): Job {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        state?.sendValue(stateOfEnding(throwable))
        stateView?.showTips(throwable.errorMsg)
        error.invoke(throwable)
    }
    return launch(context + exceptionHandler, start) {
        state?.sendValue(stateOfStarting())
        stateView?.showLoading()
        block.invoke(this)
        state?.sendValue(stateOfEnding())

        if (stateView?.showContentWhenJobDone == true) {
            stateView.showContent()
        }
    }
}
