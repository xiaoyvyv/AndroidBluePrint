
package com.xiaoyv.blueprint.json

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * 安全版本 Kt Json 解析：null -> ""
 *
 * 避免 Kotlin 非空字符串反序列化，遇到 null 强制赋值，使用时而报错
 *
 * @author why
 * @since 2022/1/11
 */
class KtAdapterFactory : TypeAdapterFactory {

    private fun Class<*>.isKotlinClass(): Boolean {
        return this.declaredAnnotations.any {
            // 只处理 kt 类
            it.annotationClass.qualifiedName == Metadata::class.java.name
        }
    }

    override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (!type.rawType.isKotlinClass()) {
            return null
        }
        val delegateAdapter = gson.getDelegateAdapter(this, type)
        return NullToEmptyStringTypeAdapter(delegateAdapter)
    }

    /**
     * 在 Json -> KtDataClass 过程中
     *
     * 将 Json 中为 null 的字符串类型转为 ""
     *
     * 避免 KtDataClass 中声明的非空 String 被强制赋予空值，导致使用而报错。
     */
    inner class NullToEmptyStringTypeAdapter<T> constructor(
        private val delegateAdapter: TypeAdapter<T>,
    ) : TypeAdapter<T>() {

        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: T?) {
            delegateAdapter.write(out, value)
        }

        @Throws(IOException::class)
        override fun read(reader: JsonReader): T? {
            val data = delegateAdapter.read(reader) ?: return null
            val gsonParseObject: Class<Any> = data.javaClass

            val defaultObject = create(gsonParseObject)

            gsonParseObject.declaredFields.forEach {
                it.isAccessible = true

                if (it.type == String::class.java && it.get(data) == null) {
                    val defaultValue = it.get(defaultObject)
                    it.set(data, defaultValue ?: "")
                }
                if (it.type == List::class.java && it.get(data) == null) {
                    val defaultValue = it.get(defaultObject)
                    it.set(data, defaultValue ?: arrayListOf<Any>())
                }
                if (it.type == Map::class.java && it.get(data) == null) {
                    val defaultValue = it.get(defaultObject)
                    it.set(data, defaultValue ?: mapOf<Any, Any>())
                }
            }
            return data
        }

        private fun create(clz: Class<Any>): Any? {
            runCatching {
                return clz.newInstance()
            }
            return null
        }
    }
}