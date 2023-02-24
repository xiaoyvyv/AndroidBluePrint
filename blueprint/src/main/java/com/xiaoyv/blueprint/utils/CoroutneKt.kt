package com.xiaoyv.blueprint.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.xiaoyv.blueprint.entity.LoadingState
import com.xiaoyv.widget.utils.ProcessLifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * IO 线程
 */
fun LifecycleOwner.launchIO(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    state: MutableLiveData<LoadingState>? = null,
    error: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launchCatch(context, start, state, error, block)
}

/**
 * UI 线程
 */
fun LifecycleOwner.launchUI(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    state: MutableLiveData<LoadingState>? = null,
    error: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launchCatch(context, start, state, error, block)
}

/**
 * IO 线程
 */
fun ViewModel.launchIO(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    error: (Throwable) -> Unit = {},
    state: MutableLiveData<LoadingState>? = null,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return viewModelScope.launchCatch(context, start, state, error, block)
}

/**
 * UI 线程
 */
fun ViewModel.launchUI(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    state: MutableLiveData<LoadingState>? = null,
    error: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
): Job {
    return viewModelScope.launchCatch(context, start, state, error, block)
}

/**
 * 全局进程
 */
fun launchProcess(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    state: MutableLiveData<LoadingState>? = null,
    error: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
): Job {
    return ProcessLifecycleScope.launchCatch(context, start, state, error, block)
}

/**
 * CoroutineScope.launchCatch()
 */
fun CoroutineScope.launchCatch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    state: MutableLiveData<LoadingState>? = null,
    error: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
): Job {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        state?.value = LoadingState.Ending
        error.invoke(throwable)
    }
    return launch(context + exceptionHandler, start) {
        withContext(Dispatchers.Main.immediate) {
            runCatching { state?.value = LoadingState.Starting }
        }
        block.invoke(this)
        state?.value = LoadingState.Ending
    }
}


