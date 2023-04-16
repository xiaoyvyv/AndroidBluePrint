package com.xiaoyv.blueprint.json

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class ParameterizedType(
    private val raw: Class<*>,
    private val args: Array<Type>? = null,
    private val owner: Type? = null,
) : ParameterizedType {

    override fun getActualTypeArguments(): Array<Type> {
        return args ?: arrayOf()
    }

    override fun getRawType(): Type {
        return raw
    }

    override fun getOwnerType(): Type? {
        return owner
    }
}