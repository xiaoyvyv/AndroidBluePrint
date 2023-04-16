@file:Suppress("UNCHECKED_CAST")

package com.xiaoyv.widget.kts

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

fun Any.findBindingCls(): Class<*> {
    val parameterizedType = (javaClass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
        ?: (javaClass.superclass.genericSuperclass as? ParameterizedType)?.actualTypeArguments
        ?: (javaClass.superclass.superclass.genericSuperclass as? ParameterizedType)?.actualTypeArguments

    val type = parameterizedType.orEmpty().find {
        val clazz = it as Class<*>
        clazz.interfaces.contains(ViewBinding::class.java)
    }
    return type as? Class<*>
        ?: throw Exception("${javaClass.name} must have a type parameter of ViewBinding!")
}


/**
 * 反射初始化 viewBinding
 */
fun <BINDING> Activity.injectViewBinding(): BINDING {
    val method = findBindingCls().getMethod("inflate", LayoutInflater::class.java)
    return method.invoke(null, LayoutInflater.from(this)) as BINDING
}

/**
 * 反射初始化 viewBinding
 */
fun <BINDING> Fragment.injectViewBinding(parent: ViewGroup? = null): BINDING {
    val method = findBindingCls().getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )
    return method.invoke(null, LayoutInflater.from(requireActivity()), parent, false) as BINDING
}
