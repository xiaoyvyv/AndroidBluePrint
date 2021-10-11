package com.xiaoyv.widget.utils

import android.app.Activity
import android.graphics.drawable.Drawable
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.Utils
import me.jessyan.autosize.utils.AutoSizeUtils

/**
 * KotlinExUtils
 *
 * @author why
 * @since 2021/02/23
 **/
object KtUtils {

    /**
     * Drawable 大小重置
     */
    @JvmStatic
    fun Drawable.resetSize(dpSize: Float): Drawable {
        val size = ConvertUtils.dp2px(dpSize)
        setBounds(size, size, size, size)
        return this
    }

    /**
     * dp转px
     */
    @JvmStatic
    val Int.dp: Int
        get() = AutoSizeUtils.dp2px(Utils.getApp(), this.toFloat())

    @JvmStatic
    val Float.dp: Int
        get() = AutoSizeUtils.dp2px(Utils.getApp(), this)

    @JvmStatic
    val Double.dp: Int
        get() = AutoSizeUtils.dp2px(Utils.getApp(), this.toFloat())

    /**
     * sp转px
     */
    @JvmStatic
    val Int.sp: Int
        get() = AutoSizeUtils.sp2px(Utils.getApp(), this.toFloat())

    @JvmStatic
    val Float.sp: Int
        get() = AutoSizeUtils.sp2px(Utils.getApp(), this)

    @JvmStatic
    val Double.sp: Int
        get() = AutoSizeUtils.sp2px(Utils.getApp(), this.toFloat())

    /**
     * 软键盘监听
     */
    @JvmStatic
    inline fun Activity.registerSoftInputChangedListener(
        crossinline onSoftInputChanged: (height: Int, isShow: Boolean) -> Unit = { _, _ -> },
    ) {
        val listener = KeyboardUtils.OnSoftInputChangedListener {
            onSoftInputChanged.invoke(it, it != 0)
        }
        KeyboardUtils.registerSoftInputChangedListener(this, listener)
    }

    /**
     * 为空替换默认值
     */
    @JvmStatic
    fun String?.orEmpty(default: String): String {
        return if (this.isNullOrBlank()) {
            default
        } else {
            this
        }
    }
}
