package com.xiaoyv.blueprint.kts

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.xiaoyv.blueprint.constant.NavKey

/**
 * 新建 Fragment
 *
 * @param index index
 */
inline fun <reified T : Fragment> fragment(
    index: Int = 0,
    bundle: Bundle = bundleOf()
): T {
    return T::class.java.newInstance().apply {
        arguments = bundle.also {
            it.putInt(NavKey.KEY_INTEGER, index)
        }
    }
}