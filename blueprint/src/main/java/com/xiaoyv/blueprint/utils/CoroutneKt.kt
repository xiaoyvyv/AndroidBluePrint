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
    start: CoroutineStart = CoroutineStart.DEFAULT,
    state: MutableLiveData<LoadingState>? = null,
    error: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launchCatch(Dispatchers.IO, start, state, error, block)
}

/**
 * UI 线程
 */
fun LifecycleOwner.launchUI(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    state: MutableLiveData<LoadingState>? = null,
    error: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
): Job {
    return lifecycleScope.launchCatch(Dispatchers.Main.immediate, start, state, error, block)
}

/**
 * IO 线程
 */
fun ViewModel.launchIO(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    error: (Throwable) -> Unit = {},
    state: MutableLiveData<LoadingState>? = null,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return viewModelScope.launchCatch(Dispatchers.IO, start, state, error, block)
}

/**
 * UI 线程
 */
fun ViewModel.launchUI(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    state: MutableLiveData<LoadingState>? = null,
    error: (Throwable) -> Unit = {},
    block: suspend CoroutineScope.() -> Unit
): Job {
    return viewModelScope.launchCatch(Dispatchers.Main.immediate, start, state, error, block)
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
        runCatching { state?.value = LoadingState.Ending }
        error.invoke(throwable)
    }
    return launch(context + exceptionHandler, start) {
        withContext(Dispatchers.Main.immediate) {
            runCatching { state?.value = LoadingState.Starting }
        }
        block.invoke(this)
        runCatching { state?.value = LoadingState.Ending }
    }
}


