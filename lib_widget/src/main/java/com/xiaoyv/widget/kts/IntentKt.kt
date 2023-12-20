package com.xiaoyv.widget.kts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.core.os.bundleOf
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import kotlin.reflect.KClass

/**
 * IntentKt
 */
fun <T> T.launchBy(launcher: ActivityResultLauncher<T>) {
    runCatching {
        launcher.launch(this)
    }.onFailure {
        showToastCompat(it.errorMsg)
    }
}

@JvmOverloads
fun <T : Context> KClass<T>.createIntent(bundle: Bundle = bundleOf()): Intent =
    Intent(Utils.getApp(), java).apply { putExtras(bundle) }
