package com.xiaoyv.widget.kts

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

val CoroutineScope.isJobCanceled
    get() = coroutineContext[Job]?.isCancelled == true

fun <T> MutableLiveData<T>.sendValue(sendValue: T) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        value = sendValue
    } else {
        postValue(sendValue)
    }
}

