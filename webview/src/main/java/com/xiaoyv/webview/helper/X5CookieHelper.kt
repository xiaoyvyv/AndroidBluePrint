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

    /*   fun readLocalStorage(context: Context) {
           try {
               val localStorageDir =
                   PathUtils.getInternalAppDataPath() + "/app_x5webview/Default/Local Storage"
               LocalStorage(FileUtils.getFileByPath(localStorageDir)).use { localStorage ->
                   val db = localStorage.db

                   val origin = "https://web.xiaoyv.com.cn"
                   val encodedOrigin: ByteArray = origin.toByteArray(StandardCharsets.UTF_8)
                   val prefixLen = encodedOrigin.size + 2
                   val prefix = ByteArray(prefixLen)
                   System.arraycopy(encodedOrigin, 0, prefix, 1, encodedOrigin.size)
                   prefix[0] = '_'.code.toByte()
                   prefix[prefix.size - 1] = 0

                   val key = "Hello".toByteArray().toMutableList().apply { add(0, 1) }
                   val value = "World".toByteArray().toMutableList().apply { add(0, 1) }.toByteArray()

                   val insertKey = prefix.toMutableList().apply { addAll(key) }.toByteArray()

                   LogUtils.e(insertKey.decodeToString(), value.decodeToString())
                   db.put(insertKey, value)

                   val iterator = db.iterator()
                   while (iterator.hasNext()) {
                       val data = iterator.next()
                       LogUtils.e(data.key, data.value)
                   }
                   //localStorage.put("https://web.xiaoyv.com.cn", "asfasfasfsafa")

                   localStorage.origins().use { origins ->
                       while (origins.hasNext()) {
                           val origin: String = origins.next()

                           localStorage.dataForOrigin(origin).use { entries ->
                               while (entries.hasNext()) {
                                   val (key, value) = entries.next()
                                   LogUtils.e("Origin: $origin, $key = $value")
                               }
                           }
                       }
                   }
               }
           } catch (e: IOException) {
               LogUtils.e("oh no", e)
           }
       }
   */

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