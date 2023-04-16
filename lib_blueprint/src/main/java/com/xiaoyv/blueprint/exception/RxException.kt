@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.exception

/**
 * RxException
 *
 * @author why
 * @since 2020/11/11
 */
class RxException(override var message: String?) : Exception(message.orEmpty()) {
    /**
     * 错误码
     */
    var code: Int = DEFAULT_ERROR

    constructor(code: Int, msg: String?) : this(msg.orEmpty()) {
        this.code = code
        this.message = msg
    }

    companion object {
        const val DEFAULT_ERROR = -1
    }
}