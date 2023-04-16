package com.xiaoyv.widget.webview.listener;


/**
 * 网页加载超时
 *
 * @author 王怀玉
 * @since 2020/11/11
 */
public interface OnWebTimeoutListener {
    /**
     * 网页加载超时
     */
    default void onTimeout() {

    }
}