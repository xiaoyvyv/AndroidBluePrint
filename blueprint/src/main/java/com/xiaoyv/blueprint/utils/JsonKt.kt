package com.xiaoyv.blueprint.utils

import com.blankj.utilcode.util.GsonUtils


fun Any?.toJson(pretty: Boolean = false): String {
    if (pretty) {
        return GsonUtils.toJson(GsonUtils.getGson().newBuilder().setPrettyPrinting().create(), this)
    }
    return GsonUtils.toJson(this)
}