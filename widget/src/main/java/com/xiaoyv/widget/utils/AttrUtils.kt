package com.xiaoyv.widget.utils

import android.util.TypedValue
import androidx.annotation.AttrRes
import com.blankj.utilcode.util.Utils

/**
 * AttrUtils
 *
 * @author why
 * @since 2020/12/15
 */
object AttrUtils {

    @JvmStatic
    fun getAttrColor(@AttrRes attrRes: Int): Int {
        val typedValue = TypedValue()
        Utils.getApp().theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue.data
    }
}