package com.xiaoyv.widget.kts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

val CoroutineScope.isJobCanceled
    get() = coroutineContext[Job]?.isCancelled == true