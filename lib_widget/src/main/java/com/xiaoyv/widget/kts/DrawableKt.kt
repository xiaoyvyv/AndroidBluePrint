package com.xiaoyv.widget.kts

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.*
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.blankj.utilcode.util.ConvertUtils

/**
 * DrawableUtils
 *
 * @author why
 * @since 2021/01/02
 */
object DrawableUtils {
    /**
     * 创建一张指定大小的纯色图片，支持圆角
     *
     * @param resources    Resources对象，用于创建BitmapDrawable
     * @param width        图片的宽度
     * @param height       图片的高度
     * @param cornerRadius 图片的圆角，不需要则传0
     * @param filledColor  图片的填充色
     * @return 指定大小的纯色图片
     */
    @JvmStatic
    fun createDrawableWithSize(
        resources: Resources?,
        width: Int,
        height: Int,
        cornerRadius: Int,
        @ColorInt filledColor: Int
    ): BitmapDrawable {
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        if (cornerRadius > 0) {
            val paint = Paint()
            paint.isAntiAlias = true
            paint.style = Paint.Style.FILL
            paint.color = filledColor
            canvas.drawRoundRect(
                RectF(0f, 0f, width.toFloat(), height.toFloat()),
                cornerRadius.toFloat(),
                cornerRadius.toFloat(),
                paint
            )
        } else {
            canvas.drawColor(filledColor)
        }
        return BitmapDrawable(resources, output)
    }

    /**
     * 设置Drawable的颜色
     * **这里不对Drawable进行mutate()，会影响到所有用到这个Drawable的地方，如果要避免，请先自行mutate()**
     *
     *
     * please use [DrawableCompat.setTint] replace this.
     */
    @JvmStatic
    @Deprecated("")
    fun setDrawableTintColor(drawable: Drawable?, @ColorInt tintColor: Int): ColorFilter {
        val colorFilter = LightingColorFilter(Color.argb(255, 0, 0, 0), tintColor)
        if (drawable != null) {
            drawable.colorFilter = colorFilter
        }
        return colorFilter
    }

    /**
     * 由一个drawable生成bitmap
     */
    @JvmStatic
    fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        } else if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val intrinsicWidth = drawable.intrinsicWidth
        val intrinsicHeight = drawable.intrinsicHeight
        return if (!(intrinsicWidth > 0 && intrinsicHeight > 0)) {
            null
        } else try {
            val config =
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, config)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 创建一张渐变图片，支持韵脚。
     *
     * @param startColor 渐变开始色
     * @param endColor   渐变结束色
     * @param radius     圆角大小
     * @param centerX    渐变中心点 X 轴坐标
     * @param centerY    渐变中心点 Y 轴坐标
     * @return 返回所创建的渐变图片。
     */
    @JvmStatic
    fun createCircleGradientDrawable(
        @ColorInt startColor: Int,
        @ColorInt endColor: Int, radius: Int,
        @FloatRange(from = 0.0, to = 1.0) centerX: Float,
        @FloatRange(from = 0.0, to = 1.0) centerY: Float,
    ): GradientDrawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.colors = intArrayOf(
            startColor,
            endColor
        )
        gradientDrawable.gradientType = GradientDrawable.RADIAL_GRADIENT
        gradientDrawable.gradientRadius = radius.toFloat()
        gradientDrawable.setGradientCenter(centerX, centerY)
        return gradientDrawable
    }

    /**
     * 圆角
     *
     * @param color  color
     * @param radius 单位dp
     * @return 圆角
     */
    @JvmStatic
    fun createSolidDrawable(@ColorInt color: Int, radius: Float): GradientDrawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColor(color)
        gradientDrawable.cornerRadius = radius.dpi.toFloat()
        return gradientDrawable
    }

    /**
     * 圆角
     *
     * @param color  边的颜色
     * @param radius 单位dp
     * @return 圆角
     */
    @JvmStatic
    fun createStrokeDrawable(@ColorInt color: Int, width: Int, radius: Float): GradientDrawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setStroke(width, color)
        gradientDrawable.cornerRadius = radius.dpi.toFloat()
        return gradientDrawable
    }

    /**
     * 圆角
     *
     * @param strokeColor 边的颜色
     * @param filledColor 填充颜色
     * @param radius      单位dp
     * @return 圆角
     */
    @JvmStatic
    fun createStrokeDrawable(
        @ColorInt strokeColor: Int,
        @ColorInt filledColor: Int,
        width: Int,
        radius: Float
    ): GradientDrawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setStroke(width, strokeColor)
        gradientDrawable.setColor(filledColor)
        gradientDrawable.cornerRadius = radius.dpi.toFloat()
        return gradientDrawable
    }

    /**
     * 动态创建带上分隔线或下分隔线的Drawable。
     *
     * @param separatorColor 分割线颜色。
     * @param bgColor        Drawable 的背景色。
     * @param top            true 则分割线为上分割线，false 则为下分割线。
     * @return 返回所创建的 Drawable。
     */
    @JvmStatic
    fun createItemSeparatorBg(
        @ColorInt separatorColor: Int,
        @ColorInt bgColor: Int,
        separatorHeight: Int,
        top: Boolean
    ): LayerDrawable {
        val separator = ShapeDrawable()
        separator.paint.style = Paint.Style.FILL
        separator.paint.color = separatorColor
        val bg = ShapeDrawable()
        bg.paint.style = Paint.Style.FILL
        bg.paint.color = bgColor
        val layers = arrayOf<Drawable>(separator, bg)
        val layerDrawable = LayerDrawable(layers)
        layerDrawable.setLayerInset(
            1, 0, if (top) separatorHeight else 0, 0, if (top) 0 else separatorHeight
        )
        return layerDrawable
    }


    @JvmStatic
    fun createSelectorDrawable(
        normal: Drawable,
        pressed: Drawable? = null,
        disable: Drawable? = null,
    ): StateListDrawable {
        return StateListDrawable().also { bg ->
            if (pressed != null) {
                bg.addState(intArrayOf(android.R.attr.state_pressed), pressed)
            }
            if (disable != null) {
                bg.addState(intArrayOf(-android.R.attr.state_enabled), disable)
            }
            bg.addState(intArrayOf(), normal)
        }
    }
}

/**
 * Drawable 大小重置
 */
fun Drawable.resetSize(dpSize: Float): Drawable {
    val size = ConvertUtils.dp2px(dpSize)
    setBounds(size, size, size, size)
    return this
}