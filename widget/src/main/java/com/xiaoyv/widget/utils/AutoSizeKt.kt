/*
 * Copyright 2018 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaoyv.widget.utils

import android.content.Context
import android.util.TypedValue
import kotlin.math.roundToInt

/**
 * AutoSizeUtils
 */
object AutoSizeKt {
    @JvmStatic
    fun dp2px(context: Context, value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            context.resources.displayMetrics
        ).roundToInt()
    }

    @JvmStatic
    fun sp2px(context: Context, value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            value,
            context.resources.displayMetrics
        ).roundToInt()
    }

    @JvmStatic
    fun pt2px(context: Context, value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_PT,
            value,
            context.resources.displayMetrics
        ).roundToInt()
    }

    @JvmStatic
    fun in2px(context: Context, value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_IN,
            value,
            context.resources.displayMetrics
        ).roundToInt()
    }

    @JvmStatic
    fun mm2px(context: Context, value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_MM,
            value,
            context.resources.displayMetrics
        ).roundToInt()
    }
}
