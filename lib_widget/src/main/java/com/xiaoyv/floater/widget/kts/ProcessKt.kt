package com.xiaoyv.floater.widget.kts

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope

/**
 * ProcessLifecycleScope
 */
val ProcessLifecycleScope: LifecycleCoroutineScope
    get() = ProcessLifecycleOwner.get().lifecycleScope