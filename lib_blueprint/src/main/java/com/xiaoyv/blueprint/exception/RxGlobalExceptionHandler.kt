package com.xiaoyv.blueprint.exception

/**
 * 默认异常处理器
 *
 * @author why
 * @since 2020/11/29
 */
class RxGlobalExceptionHandler : IExceptionHandler {

    override fun handleException(e: Throwable): RxException {
        return RxException(RxException.DEFAULT_ERROR, e.message.toString())
    }
}