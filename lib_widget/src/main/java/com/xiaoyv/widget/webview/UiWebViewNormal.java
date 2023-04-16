package com.xiaoyv.widget.webview;

import android.content.Context;
import android.util.AttributeSet;

/**
 * X5WebView 内置H5加载用，仅用于加载网页链接，无其他需求
 * <p>
 * 不进行模板内嵌相关适配，加快加载速度
 *
 * @author why
 * @since 2020/11/11
 */

public class UiWebViewNormal extends UiWebView {

    public UiWebViewNormal(Context context) {
        super(context);
    }

    public UiWebViewNormal(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public UiWebViewNormal(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

}
