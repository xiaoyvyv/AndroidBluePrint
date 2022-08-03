package com.xiaoyv.blueprint.utils

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

/**
 * CoroutineExceptionHandler
 */
val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
    coroutineContext.cancel()
    LogUtils.e(throwable)

    ToastUtils.showShort("error: $throwable")
}

/**
 * 主线程 CoroutineContext
 */
val mainCoroutineContext = Dispatchers.Main + errorHandler

/**
 * IO 线程 CoroutineContext
 */
val ioCoroutineContext = Dispatchers.IO + errorHandler