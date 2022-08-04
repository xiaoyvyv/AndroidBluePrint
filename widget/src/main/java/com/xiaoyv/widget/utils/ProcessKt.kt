package com.xiaoyv.widget.utils

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope

/**
 * ProcessLifecycleScope
 */
val ProcessLifecycleScope: LifecycleCoroutineScope
    get() = ProcessLifecycleOwner.get().lifecycleScope