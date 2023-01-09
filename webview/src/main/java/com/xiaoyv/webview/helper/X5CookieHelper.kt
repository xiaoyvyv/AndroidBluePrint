package com.xiaoyv.webview.helper

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils
import com.tencent.smtt.sdk.CookieManager


/**
 * X5CookieHelper
 *
 * @author why
 * @since 2023/1/7
 */
object X5CookieHelper {
    private const val COOKIE_SPLIT_SYMBOL = ";"

    fun readCookie(url: String): String {
        val cookieManager = CookieManager.getInstance()
        return cookieManager.getCookie(url).orEmpty().trim()
    }

    fun setCookie(url: String, cookie: String) {
        ThreadUtils.getCachedPool().submit {
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)

            cookie.trim().split(COOKIE_SPLIT_SYMBOL).filter { it.contains("=") }.onEach {

                val cookieItem = it.split("=")
                val cookieName = cookieItem.getOrNull(0).orEmpty().trim()
                val cookieValue = cookieItem.getOrNull(1).orEmpty().trim()

                LogUtils.e(it, cookieName, cookieValue)

                if (cookieName.isNotBlank()) {
                    cookieManager.setCookie(url, it, true)
                }
            }

            cookieManager.flush()
        }
    }
}