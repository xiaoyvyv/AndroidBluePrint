@file:Suppress("UNCHECKED_CAST")

package com.xiaoyv.floater.widget.kts

import java.util.*
import kotlin.math.min

fun <T> List<T>?.copyOf(): List<T> {
    val original = this.orEmpty()
    return mutableListOf<T>().apply { addAll(original) }
}

fun <T> List<T>?.mutableCopyOf(): MutableList<T> {
    val original = this.orEmpty()
    return mutableListOf<T>().apply { addAll(original) }
}

fun <T> List<T>?.copyAdd(append: T): MutableList<T> {
    return mutableCopyOf().apply { add(append) }
}

fun <T> List<T>?.copyAddAll(append: List<T>, index: Int? = null): MutableList<T> {
    return mutableCopyOf().apply {
        if (index != null && index >= 0 && index <= append.size) {
            addAll(index, append)
        } else {
            addAll(append)
        }
    }
}

fun <T> List<T>?.copyDelete(delete: T?): MutableList<T> {
    val deleteItem = delete ?: return mutableCopyOf()
    return mutableCopyOf().apply { remove(deleteItem) }
}

fun <T> List<T>?.asAnyList(): MutableList<Any> {
    return (this as? MutableList<Any>) ?: arrayListOf<Any>().also {
        it.addAll(listOf(orEmpty()))
    }
}

/**
 * 截取 list
 */
fun <T : Any> List<T>.subListLimit(limit: Int): MutableList<T> {
    return subList(0, min(size, limit)).toMutableList()
}
