@file:Suppress("DEPRECATION")

package com.xiaoyv.widget.kts

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

inline fun <reified T : Serializable> Bundle.getSerialObj(key: String, default: T): T {
    return getSerialObj(key) ?: default
}

inline fun <reified T : Serializable> Bundle.getSerialObj(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, T::class.java)
    } else {
        getSerializable(key) as? T
    }
}


inline fun <reified T : Parcelable> Bundle.getParcelObj(key: String, default: T): T {
    return getParcelObj(key) ?: default
}

inline fun <reified T : Parcelable> Bundle.getParcelObj(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        getParcelable(key) as? T
    }
}