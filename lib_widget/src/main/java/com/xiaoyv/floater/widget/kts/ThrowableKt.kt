package com.xiaoyv.floater.widget.kts

/**
 * ThrowableKt
 *
 * @author why
 * @since 2023/4/16
 */

val Throwable.errorMsg: String
    get() = cause?.cause?.cause?.message.orEmpty()
        .ifBlank {
            cause?.cause?.message.orEmpty()
        }.ifBlank {
            cause?.message.orEmpty()
        }.ifBlank {
            message.orEmpty()
        }