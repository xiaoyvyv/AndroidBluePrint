package com.xiaoyv.blueprint.exception;

public interface IExceptionHandler {

    /**
     * 处理过滤错误信息
     *
     * @param e e
     * @return RxException
     */
    RxException handleException(Throwable e);
}