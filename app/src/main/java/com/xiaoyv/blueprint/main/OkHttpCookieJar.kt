package com.xiaoyv.blueprint.main

import com.tencent.smtt.sdk.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * OkHttpCookieJarImpl
 *
 * @author why
 * @since 2023/01/01
 */
class OkHttpCookieJar : CookieJar {

    private val cookieManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        CookieManager.getInstance().apply { setAcceptCookie(true) }
    }

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookies.onEach {
            saveCookie(url, it)
        }
    }

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return loadCookie(url).apply {
        }
    }

    @Synchronized
    private fun saveCookie(httpUrl: HttpUrl, cookie: Cookie) {
        if (cookie.isExpired()) return

        val link = httpUrl.toString()
        val cookieStr = cookie.toString()

        cookieManager.setAcceptCookie(true)
        cookieManager.setCookie(link, cookieStr)
        cookieManager.flush()
    }

    @Synchronized
    private fun loadCookie(httpUrl: HttpUrl): List<Cookie> {
        val link = httpUrl.toString()

        // CookieManager 获取到的时间都是过滤了过期的
        val cacheCookie = cookieManager.getCookie(link).orEmpty().trim()

        return cacheCookie.split(";")
            .filter {
                it.contains("=") && it.split('=').firstOrNull()
                    .orEmpty()
                    .trim()
                    .isNotBlank()
            }
            .mapNotNull {
                Cookie.parse(httpUrl, it)
            }
    }

    /**
     * 当前cookie是否过期
     */
    private fun Cookie.isExpired(): Boolean {
        return expiresAt < System.currentTimeMillis()
    }
}