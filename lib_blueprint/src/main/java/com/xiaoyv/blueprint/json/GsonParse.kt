package com.xiaoyv.blueprint.json

import com.google.gson.Gson

class GsonParse {
    private var mGson: Gson? = null

    internal val requireGson: Gson
        get() = mGson ?: createGson().apply {
            mGson = this
        }

    private fun createGson(): Gson {
        return Gson().newBuilder()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .disableHtmlEscaping()
            .registerTypeAdapterFactory(KtAdapterFactory())
            .create()
    }

    companion object {
        @JvmStatic
        private val INSTANCE: GsonParse by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            GsonParse()
        }

        /**
         * 获取 Gson
         */
        @JvmStatic
        val GSON: Gson
            get() = INSTANCE.requireGson
    }
}