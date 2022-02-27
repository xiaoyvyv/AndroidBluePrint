package com.xiaoyv.widget.webview;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.xiaoyv.widget.toolbar.UiToolbar;
import com.xiaoyv.widget.utils.AdaptCompat;
import com.xiaoyv.widget.webview.listener.OnFileChooseListener;
import com.xiaoyv.widget.webview.listener.OnProgressChangeListener;
import com.xiaoyv.widget.webview.listener.OnWebLoadFinishListener;
import com.xiaoyv.widget.webview.listener.OnWebTimeoutListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.jessyan.autosize.AutoSizeCompat;
import me.jessyan.autosize.utils.AutoSizeUtils;
import okhttp3.Cookie;


/**
 * BvWebView
 *
 * @author why
 * @since 2020/11/11
 */

public class UiWebView extends WebView {
    private static final String TAG = "BvWebView";
    private static final String EMPTY = "about:blank";
    private static final long TASK_DELAY = 200;
    private static final int MISTAKE = 100;
    protected Context context;
    protected UiToolbar toolbar;
    protected UiWebProgressBar progressBar;
    protected OnWebLoadFinishListener onWebLoadFinishListener;
    protected OnProgressChangeListener onProgressChangeListener;
    protected OnFileChooseListener onFileChooseListener;
    protected OnWebTimeoutListener onWebTimeoutListener;
    protected String referer;
    protected UiWebDownloadListener downloadListener;
    protected WebSettings webSetting;
    protected Runnable timeoutTask;
    protected long timeout = 20000;
    /**
     * 是否需要拦截请求替换前置服务
     */
    protected boolean needIntercept = false;

    private OnAcceptResetHeightConsumer onAcceptResetHeightConsumer;

    private boolean isMonitoring = false;

    private boolean isError = false;

    private int webHeight = -1;

    /**
     * 若 WebView 加载的 H5 高度为浮动不固定的话，开启高度适配后，将以下值作为 H5 最低高度
     */
    private final int dp600 = AutoSizeUtils.dp2px(Utils.getApp(), 600);

    private final List<String> urlHistory = new ArrayList<>();

    private final Runnable resetHeightTask = new Runnable() {
        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            if (isStopMonitorAndDestroy()) {
                return;
            }

            evaluateJavascript("document.getElementsByTagName('body')[0].offsetHeight", value -> {
                if (isStopMonitorAndDestroy()) {
                    return;
                }
                float scale = getScale();
                float rawMistake = MISTAKE / scale;
//                LogUtils.i("JS 回调内容高度", "js: " + value + " native-get: " + getContentHeight() + " native-scale: " + scale + " rawMistake: " + rawMistake);
                try {
                    int htmlHeight = (int) (Integer.parseInt(value) * scale);
                    int contentHeight = htmlHeight + MISTAKE;
                    float newMistake = (contentHeight - webHeight) * 1f / scale;
                    // 判断HTML 是否为浮动，即高度会不固定
                    boolean isHtmlFloat = Math.abs(newMistake - rawMistake) < 10;
                    // 设置浮动的最小高度
                    if (contentHeight < dp600 && isHtmlFloat) {
                        contentHeight = dp600;
                        // 先清除标志位用来重置高度
                        isHtmlFloat = false;
                    }
                    if (contentHeight > 0 && webHeight != contentHeight && !isHtmlFloat) {
                        // 是否能重置高度，如 WebView 被嵌套在 ScrollView，ScrollView 滑动时不要重置高度，不然会抖动
                        if (onAcceptResetHeightConsumer == null || onAcceptResetHeightConsumer.canReset()) {
                            webHeight = contentHeight;
                            resetHeight(webHeight);
                        }
                    }
                    postDelayed(this, TASK_DELAY);
                } catch (Exception e) {
                    postDelayed(this, TASK_DELAY);
                }
            });
        }
    };


    public UiWebView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public UiWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        init();
    }

    public UiWebView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.context = context;
        init();
    }

    public void setOnAcceptResetHeightConsumer(OnAcceptResetHeightConsumer onAcceptResetHeightConsumer) {
        this.onAcceptResetHeightConsumer = onAcceptResetHeightConsumer;
    }

    public void setNeedReplaceUrl(boolean needIntercept) {
        this.needIntercept = needIntercept;
    }

    /**
     * 设置 Referer
     *
     * @param referer Referer
     */
    public void setReferer(String referer) {
        this.referer = referer;
    }

    /**
     * 设置标题栏和进度条
     *
     * @param toolbar     标题栏
     * @param progressBar 进度条
     */
    public void setTitleAndProgressBar(UiToolbar toolbar, UiWebProgressBar progressBar) {
        this.toolbar = toolbar;
        this.progressBar = progressBar;
    }

    /**
     * 设置网页加载完成监听
     *
     * @param onWebLoadFinishListener 网页加载完成监听
     */
    public void setOnWebLoadFinishListener(OnWebLoadFinishListener onWebLoadFinishListener) {
        this.onWebLoadFinishListener = onWebLoadFinishListener;
    }


    /**
     * 设置超时回调
     *
     * @param onWebTimeoutListener 超时回调
     */
    public void setOnWebTimeoutListener(OnWebTimeoutListener onWebTimeoutListener) {
        this.onWebTimeoutListener = onWebTimeoutListener;
    }

    /**
     * 设置超时时间
     *
     * @param timeout 超时时间
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * 初始化
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void init() {
        initWebViewSettings();
        initWebView();
    }

    /**
     * 初始化
     */
    protected void initWebView() {
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.setWebViewClient(mWebViewClient);
        this.setWebChromeClient(mWebChromeClient);
        this.downloadListener = new UiWebDownloadListener(context);
        this.setDownloadListener(this.downloadListener);
    }

    /**
     * 根据WebView 内容重置 WebView 高度
     */
    @SuppressWarnings("deprecation")
    public void resetHeight() {
        int contentHeight = (int) (getContentHeight() * getScale());
        if (contentHeight == 0) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = contentHeight;
            setLayoutParams(layoutParams);
        }
    }

    /**
     * 根据WebView 内容重置 WebView 高度
     */
    public void resetHeight(int contentHeight) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = contentHeight;
            setLayoutParams(layoutParams);
        }
    }

    /**
     * 同步Cookie
     */
    protected void syncCookie(List<Cookie> cookies) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookies(value -> {

        });

        for (Cookie cookie : cookies) {
            String cookieName = cookie.name();
            String cookieValue = cookie.value();
            if (!StringUtils.isEmpty(cookieName) && !StringUtils.isEmpty(cookieValue)) {
                String url = cookie.domain() + cookie.path();
                String newCookie = cookie.name() + "=" + cookie.value();
                cookieManager.setCookie(url, newCookie);
                // 同步Cookie
                cookieManager.flush();
            }
        }
    }


    /**
     * 设置单独 Cookie
     *
     * @param url    url
     * @param cookie cookie
     */
    public void setCookie(String url, String cookie) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, cookie);
        cookieManager.flush();
    }


    /**
     * 加载Html
     *
     * @param html html
     */
    public void loadHtml(String html) {
        this.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

    /**
     * 加载Html
     *
     * @param baseUrl baseUrl
     * @param html    html
     */
    public void loadHtml(String baseUrl, String html) {
        this.loadDataWithBaseURL(baseUrl, html, "text/html", "utf-8", null);
    }

    public OnProgressChangeListener getOnProgressChangeListener() {
        return onProgressChangeListener;
    }

    public void setOnProgressChangeListener(OnProgressChangeListener onProgressChangeListener) {
        this.onProgressChangeListener = onProgressChangeListener;
    }

    public OnFileChooseListener getOnFileChooseListener() {
        return onFileChooseListener;
    }

    public void setOnFileChooseListener(OnFileChooseListener onFileChooseListener) {
        this.onFileChooseListener = onFileChooseListener;
    }

    /**
     * 初始化WebView设置
     */
    @SuppressLint("SetJavaScriptEnabled")
    protected void initWebViewSettings() {
        webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);

        webSetting.setSupportMultipleWindows(false);
        // 设置屏幕适应，将图片调整到适合WebView的大小
        webSetting.setUseWideViewPort(true);
        // 设置屏幕适应，缩放至屏幕的大小
        webSetting.setLoadWithOverviewMode(true);

        // 缩放操作
        webSetting.setSupportZoom(true);
        // 设置使用内置的缩放控件
        webSetting.setBuiltInZoomControls(true);
        // 隐藏原生的缩放控件
        webSetting.setDisplayZoomControls(false);

        // 其他细节操作
        webSetting.setAllowFileAccess(true);
        webSetting.setAllowContentAccess(true);
        webSetting.setAllowFileAccessFromFileURLs(true);
        // 支持通过JS打开新窗口
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        // 支持自动加载图片
        webSetting.setLoadsImagesAutomatically(true);
        // 设置编码格式
        webSetting.setDefaultTextEncodingName("UTF-8");
        webSetting.setGeolocationEnabled(true);
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setBlockNetworkImage(false);
        // 取消系统字体缩放
        webSetting.setTextZoom(100);

        // 设置混合协议
        webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    }

    /**
     * 设置缓存模式
     *
     * @param cacheMode 缓存模式
     */
    public void setCacheMode(int cacheMode) {
        webSetting.setCacheMode(cacheMode);
    }

    @Override
    public void setOverScrollMode(int mode) {
        super.setOverScrollMode(mode);
        AutoSizeCompat.autoConvertDensity(super.getResources(), AdaptCompat.ADAPT_WIDTH_DP, true);
    }

    /**
     * 客户端设置
     */
    public WebViewClient mWebViewClient = new WebViewClient() {


        /**
         * 返回值解释
         *
         * 若该方法返回 true ，则说明由应用的代码处理该 url，WebView 不处理，也就是程序员自己做处理
         * 若该方法返回 false，则说明由 WebView 处理该 url，即用 WebView 加载该 url
         *
         * 返回 false 历史记录不会保存重定向的网页（可以避免重复加载的问题）
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (!urlHistory.contains(url)) {
                urlHistory.add(url);
            }

            // 判断 url 是否为 SchemaUrl
            if (!URLUtil.isNetworkUrl(url)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // 查询已安装的软件是否可以处理该 Intent
                if (IntentUtils.isIntentAvailable(intent)) {
                    Snackbar.make(UiWebView.this, "该网页想要打开外部App", Snackbar.LENGTH_INDEFINITE)
                            .setAction("打开", v -> context.startActivity(intent))
                            .show();
                }
                // 返回 true,代表已经处理过该 url
                return true;
            }

            // 添加 Referer
            String webUrl = getUrl();
            Map<String, String> extraHeaders = new HashMap<>(0);
            extraHeaders.put("Referer", StringUtils.isEmpty(referer) ? webUrl : referer);
            view.loadUrl(url, extraHeaders);
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            LogUtils.i(TAG, "onPageStarted: " + url);
            if (EMPTY.equals(url)) {
                return;
            }
            isError = false;

            clearTask();
            timeoutTask = () -> {
                if (!isAttachedToWindow()) {
                    return;
                }
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    if (activity.isDestroyed() || activity.isFinishing()) {
                        return;
                    }
                }
                stopLoading();

                String originalUrl = getOriginalUrl();

                LogUtils.e(TAG, "超时链接: " + originalUrl + "\n" + getUrl() + "\n" + url);

                if (onWebTimeoutListener != null) {
                    onWebTimeoutListener.onTimeout();
                }
            };

            postDelayed(timeoutTask, timeout);

            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            super.onPageFinished(webView, url);
            LogUtils.i(TAG, "onPageFinished: " + url);
            if (EMPTY.equals(url)) {
                return;
            }

            // 清除超时监听
            clearTask();

            if (onWebLoadFinishListener != null && getProgress() == 100 && !isError) {
                onWebLoadFinishListener.onLoadFinish(webView, progressBar, url);
            }

        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            // 清除超时监听
            clearTask();

            int statusCode = errorResponse.getStatusCode();
            String requestUrl = request.getUrl().toString();

            LogUtils.e(TAG, "onReceivedHttpError: " + requestUrl + "\nURL: " + Arrays.toString(urlHistory.toArray()));

            // 是否加载失败
            checkIsLoadError(requestUrl, statusCode, errorResponse.getReasonPhrase());
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            // 清除超时监听
            clearTask();

            String requestUrl = request.getUrl().toString();
            int errorCode = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                errorCode = error.getErrorCode();
            }
            String description = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                description = String.valueOf(error.getDescription());
            }

            LogUtils.e(TAG, "onReceivedError: " + requestUrl + "\nURL: " + Arrays.toString(urlHistory.toArray()));

            // 是否加载失败
            checkIsLoadError(requestUrl, errorCode, description);
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            sslErrorHandler.proceed();
        }

        /**
         * 若加载目标主链接失败才回调错误，H5资源链接失败不回调
         *
         * @param requestUrl url
         * @param statusCode statusCode
         * @param failMsg    failMsg
         */
        private void checkIsLoadError(String requestUrl, int statusCode, String failMsg) {
            if (requestUrl == null) {
                return;
            }
            if (urlHistory.contains(requestUrl) || urlHistory.contains(requestUrl.replace("/?", "?"))) {
                stopLoading();
                if (onWebLoadFinishListener != null) {
                    isError = true;
                    onWebLoadFinishListener.onLoadError(statusCode, String.valueOf(failMsg));
                }
            }
        }
    };

    private void clearTask() {
        if (timeoutTask != null) {
            removeCallbacks(timeoutTask);
            timeoutTask = null;
        }
    }

    /**
     * 辅助类
     */
    public WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView webView, String title) {
            if (toolbar != null) {
                toolbar.setTitle(StringUtils.isEmpty(title) ? getUrl() : title);
            }
        }

        @Override
        public void onProgressChanged(WebView webView, int progress) {
            if (progressBar != null) {
                progressBar.setProgress(progress);
            }

            if (onProgressChangeListener != null) {
                onProgressChangeListener.onProgressChanged(webView, progress);
            }
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
            if (onFileChooseListener != null) {
                onFileChooseListener.onShowFileChooser(webView, valueCallback, fileChooserParams);
            } else {
                valueCallback.onReceiveValue(null);
            }
            return true;
        }
    };

    @Override
    public void reload() {
        if (EMPTY.equals(getUrl())) {
            goBack();
            return;
        }
        super.reload();
    }

    @Override
    public void loadUrl(@NonNull String url) {
        if (!urlHistory.contains(url)) {
            urlHistory.add(url);
        }
        super.loadUrl(url);
    }

    /**
     * 是否销毁
     *
     * @return 是否销毁
     */
    public boolean isStopMonitorAndDestroy() {
        return !isMonitoring || !isAttachedToWindow();
    }

    public boolean isError() {
        return isError;
    }

    @Override
    public void destroy() {
        // 清除超时监听
        clearTask();
        stopMonitor();
        loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        clearHistory();
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            parent.removeView(this);
        }
        super.destroy();
    }

    /**
     * 高度监控实时回调，重置WebView高度
     */
    public void startMonitor() {
        stopMonitor();
        isMonitoring = true;
        postDelayed(resetHeightTask, TASK_DELAY);
    }

    public void stopMonitor() {
        isMonitoring = false;
        removeCallbacks(resetHeightTask);
    }

    public interface OnAcceptResetHeightConsumer {
        /**
         * canReset
         *
         * @return canReset
         */
        boolean canReset();
    }

}
