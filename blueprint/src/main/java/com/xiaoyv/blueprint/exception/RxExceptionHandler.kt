package com.xiaoyv.blueprint.exception

/**
 * 全局 RxJava 异常处理器
 */
object RxExceptionHandler {
    /**
     * 默认的错误信息处理者
     */
    private var exceptionHandler: IExceptionHandler? = null

    /**
     * 设置错误信息处理者
     *
     * @param handler exceptionHandler
     */
    @JvmStatic
    fun setExceptionHandler(handler: IExceptionHandler) {
        exceptionHandler = handler
    }

    /**
     * 处理错误信息
     *
     * @param e e
     * @return RxException
     */
    @JvmStatic
    fun handleException(e: Throwable): RxException {
        return exceptionHandler?.handleException(e) ?: RxException(e.message.toString())
    }
}