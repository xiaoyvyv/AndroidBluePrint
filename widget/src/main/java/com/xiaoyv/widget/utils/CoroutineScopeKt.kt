package com.xiaoyv.widget.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

val CoroutineScope.isJobCanceled
    get() = coroutineContext[Job]?.isCancelled == true