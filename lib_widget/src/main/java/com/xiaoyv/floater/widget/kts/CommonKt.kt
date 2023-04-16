package com.xiaoyv.floater.widget.kts

/**
 * CommonKt
 *
 * @author why
 * @since 2023/3/13
 */
inline fun <T, R> useNotNull(any: T?, default: T? = null, block: T.() -> R): R? {
    return (any ?: default)?.let { block.invoke(it) }
}
