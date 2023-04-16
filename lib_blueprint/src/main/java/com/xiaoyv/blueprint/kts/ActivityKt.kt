package com.xiaoyv.blueprint.kts

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import androidx.core.os.bundleOf
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.xiaoyv.blueprint.constant.NavKey
import java.io.Serializable
import kotlin.reflect.KClass

val <T : Activity> T.activity get() = this

inline fun <reified T : Activity> KClass<T>.openSerializable(
    param1: Serializable? = null,
    param2: Serializable? = null
) {
    open(paramSerializable1 = param1, paramSerializable2 = param2)
}

inline fun <reified T : Activity> KClass<T>.openString(
    param1: String? = null,
    param2: String? = null
) {
    open(paramString1 = param1, paramString2 = param2)
}

inline fun <reified T : Activity> KClass<T>.open(bundle: Bundle = Bundle.EMPTY) {
    ActivityUtils.startActivity(bundle, java)
}

inline fun <reified T : Activity> KClass<T>.open(
    paramString1: String? = null,
    paramString2: String? = null,
    paramInteger1: Int? = null,
    paramInteger2: Int? = null,
    paramLong1: Long? = null,
    paramLong2: Long? = null,
    paramFloat1: Float? = null,
    paramFloat2: Float? = null,
    paramDouble1: Double? = null,
    paramDouble2: Double? = null,
    paramSerializable1: Serializable? = null,
    paramSerializable2: Serializable? = null,
    paramBoolean1: Boolean? = null,
    paramBoolean2: Boolean? = null,
    paramParcelable1: Parcelable? = null,
    paramParcelable2: Parcelable? = null,
) {
    open(
        bundleOf(
            NavKey.KEY_STRING to paramString1,
            NavKey.KEY_STRING_SECOND to paramString2,
            NavKey.KEY_INTEGER to paramInteger1,
            NavKey.KEY_INTEGER_SECOND to paramInteger2,
            NavKey.KEY_LONG to paramLong1,
            NavKey.KEY_LONG_SECOND to paramLong2,
            NavKey.KEY_FLOAT to paramFloat1,
            NavKey.KEY_FLOAT_SECOND to paramFloat2,
            NavKey.KEY_DOUBLE to paramDouble1,
            NavKey.KEY_DOUBLE_SECOND to paramDouble2,
            NavKey.KEY_SERIALIZABLE to paramSerializable1,
            NavKey.KEY_SERIALIZABLE_SECOND to paramSerializable2,
            NavKey.KEY_BOOLEAN to paramBoolean1,
            NavKey.KEY_BOOLEAN_SECOND to paramBoolean2,
            NavKey.KEY_PARCELABLE to paramParcelable1,
            NavKey.KEY_PARCELABLE_SECOND to paramParcelable2,
        )
    )
}

/**
 * 返回按钮监听
 */
fun Activity.checkIsHomeSelected(menuItem: MenuItem): Boolean {
    if (menuItem.itemId == android.R.id.home) {
        finish()
        return true
    }
    return false
}

/**
 * 软键盘监听
 */
inline fun Activity.registerSoftInputChangedListener(
    crossinline onSoftInputChanged: (height: Int, isShow: Boolean) -> Unit = { _, _ -> },
) {
    val listener = KeyboardUtils.OnSoftInputChangedListener {
        onSoftInputChanged.invoke(it, it != 0)
    }
    KeyboardUtils.registerSoftInputChangedListener(this, listener)
}


