package com.xiaoyv.floater.widget.webview.listener;


import android.webkit.WebView;

import com.xiaoyv.floater.widget.webview.UiWebProgressBar;


/**
 * 网页加载完成监听
 *
 * @author 王怀玉
 * @since 2020/11/11
 */
public interface OnWebLoadFinishListener {
    /**
     * 加载完成回调
     *
     * @param webView     网页
     * @param progressBar 平滑进度条
     * @param url         url
     */
    void onLoadFinish(WebView webView, UiWebProgressBar progressBar, String url);

    /**
     * 加载失败
     *
     * @param error error
     */
    default void onLoadError(int code, String error) {

    }
}