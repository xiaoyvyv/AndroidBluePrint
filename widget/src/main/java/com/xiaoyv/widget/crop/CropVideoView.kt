@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.widget.crop

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.xiaoyv.widget.R
import com.xiaoyv.widget.kts.getDpx
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * CropVideoView
 *
 * @author why
 * @since 2023/1/12
 **/
class CropVideoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    /**
     * 圆角直径
     */
    var slideBarDiameter = getDpx(8f)

    /**
     * 上下边框高度
     */
    var slideBarBorderWidth: Float = getDpx(4f).toFloat()

    /**
     * 拖拽栏，左右两块触摸位置宽度
     */
    var slideBarTapWidth: Float = getDpx(22f).toFloat()

    /**
     * 左滑块的图标资源
     */
    var leftTapBlockRes: Int = R.drawable.btn_crop_left
        set(value) {
            field = value
            leftTapBlockBitmap =
                decodeResourceWithWH(context, leftTapBlockRes, getDpx(17f), getDpx(32f))
        }

    /**
     * 左滑块的图标资源 Bitmap 缓存
     */
    private var leftTapBlockBitmap =
        decodeResourceWithWH(context, leftTapBlockRes, getDpx(17f), getDpx(32f))

    /**
     * 右滑块的图标资源
     */
    var rightTapBlockRes: Int = R.drawable.btn_crop_right

    /**
     * 左滑块的图标资源 Bitmap 缓存
     */
    private var rightTapBlockBitmap =
        decodeResourceWithWH(context, rightTapBlockRes, getDpx(17f), getDpx(32f))

    /**
     * 拖拽栏颜色
     */
    var slideBarColor = Color.BLUE

    /**
     * 底部固定栏颜色（其他参数同步拖拽栏）
     */
    var backgroundBarColor = Color.GRAY

    /**
     * 进度竖线颜色
     */
    var progressLineColor = Color.WHITE

    /**
     * 进度竖线宽度
     */
    var progressLineWidth = getDpx(4f).toFloat()

    /**
     * 是否显示进度竖线
     */
    var progressLineVisible = true

    /**
     * 进度竖线触摸事件的接受宽度（主要优化触摸体验）
     */
    private val progressLineTapWidth get() = progressLineWidth * 4

    /**
     * 图片高度
     */
    var imageHeight = getDpx(56f)

    /**
     * 图片集合
     */
    var imageList = listOf<Bitmap>()
        set(value) {
            field = value.map { Bitmap.createScaledBitmap(it, imageHeight, imageHeight, true) }
            invalidate()
        }

    /**
     * 时长
     */
    var duration: Long = 12000

    /**
     * 裁剪监听
     */
    var onCropListener: OnCropListener? = null


    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isDither = true
            style = Paint.Style.FILL_AND_STROKE
            color = backgroundBarColor
        }
    }

    /**
     * 左右触摸块的相对中心方向的边界位置
     */
    private var tapLeftStart: Float = (slideBarTapWidth * 3)
    private var tapRightStart: Float = (slideBarTapWidth * 8)

    /**
     * 进度竖线横坐标
     */
    private var processLineStart: Float = tapLeftStart

    private var touchLeftBlock = false
    private var touchRightBlock = false
    private var touchProgressLine = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val totalHeight = slideBarBorderWidth * 2 + imageHeight

        setMeasuredDimension(
            getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
            totalHeight.roundToInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackgroundBar(canvas)
        drawBackgroundImages(canvas)
        drawSlideBar(canvas)

        if (progressLineVisible) {
            drawProgressLine(canvas)
        }
    }

    /**
     * 绘制画廊
     */
    private fun drawBackgroundImages(canvas: Canvas) {
        if (imageList.isEmpty()) {
            return
        }

        val spaceSize = imageList.size - 1
        val spaceWidth =
            ((width - 2 * slideBarTapWidth) - imageHeight * imageList.size) / spaceSize.toFloat()
//
//        // (imageList.size + 1) 用于多一个占位计算，避免图片覆盖右侧的底部边界滑块
//        val singleImageArea = (width - 2 * slideBarTapWidth - imageHeight / 2f) / imageList.size + 1

        imageList.forEachIndexed { index, bitmap ->
            canvas.drawBitmap(
                bitmap,
                index * spaceWidth + imageHeight * index + slideBarTapWidth,
                slideBarBorderWidth,
                paint
            )
        }
    }

    /**
     * 绘制进度线
     */
    private fun drawProgressLine(canvas: Canvas) {
        val totalHeight = slideBarBorderWidth * 2 + imageHeight

        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = progressLineWidth
        paint.style = Paint.Style.FILL
        paint.color = progressLineColor

        canvas.drawLine(
            processLineStart, progressLineWidth / 2f + slideBarBorderWidth / 2f,
            processLineStart, totalHeight - progressLineWidth / 2f - slideBarBorderWidth / 2f,
            paint
        )
    }

    /**
     * 绘制背景栏
     */
    private fun drawBackgroundBar(canvas: Canvas) {
        val totalHeight = slideBarBorderWidth * 2 + imageHeight

        paint.strokeCap = Paint.Cap.SQUARE
        paint.strokeWidth = 0f
        paint.style = Paint.Style.FILL
        paint.color = backgroundBarColor

        val bgRect = RectF(0f, 0f, width.toFloat(), totalHeight)
        canvas.drawRoundRect(
            bgRect,
            slideBarDiameter.toFloat() / 2f,
            slideBarDiameter.toFloat() / 2f,
            paint
        )

        paint.color = Color.DKGRAY
        canvas.drawRect(
            slideBarTapWidth,
            slideBarBorderWidth,
            (width.toFloat() - slideBarTapWidth),
            (totalHeight - slideBarBorderWidth),
            paint
        )
    }

    /**
     * 绘制背景栏
     */
    private fun drawSlideBar(canvas: Canvas) {
        val totalHeight = slideBarBorderWidth * 2 + imageHeight

        paint.strokeCap = Paint.Cap.SQUARE
        paint.color = slideBarColor
        paint.style = Paint.Style.FILL
        paint.strokeWidth = slideBarBorderWidth
        canvas.drawLine(
            tapLeftStart, slideBarBorderWidth / 2f,
            tapRightStart, slideBarBorderWidth / 2f,
            paint
        )
        canvas.drawLine(
            tapLeftStart, totalHeight - slideBarBorderWidth / 2f,
            tapRightStart, totalHeight - slideBarBorderWidth / 2f,
            paint
        )

        // 左侧 TAP 按钮
        val leftTapPath = Path()
        val leftX = tapLeftStart - slideBarTapWidth
        leftTapPath.moveTo(tapLeftStart, 0f)
        leftTapPath.lineTo(tapLeftStart, totalHeight)
        leftTapPath.lineTo(leftX + slideBarDiameter.toFloat(), totalHeight)
        leftTapPath.arcTo(
            leftX, totalHeight - slideBarDiameter.toFloat(),
            leftX + slideBarDiameter.toFloat(), totalHeight,
            90f, 90f,
            false
        )
        leftTapPath.lineTo(leftX, slideBarDiameter.toFloat())
        leftTapPath.arcTo(
            leftX, 0f,
            leftX + slideBarDiameter.toFloat(), slideBarDiameter.toFloat(),
            180f, 90f,
            false
        )
        leftTapPath.lineTo(leftX + slideBarDiameter.toFloat(), 0f)
        leftTapPath.close()
        canvas.drawPath(leftTapPath, paint)

        leftTapBlockBitmap?.also {
            val btnWidth = it.width
            val btnHeight = it.height
            canvas.drawBitmap(
                it,
                leftX + (slideBarTapWidth - btnWidth) / 2f,
                (totalHeight - btnHeight) / 2f,
                paint
            )
        }

        // 右侧 TAP 按钮
        val rightTapPath = Path()
        rightTapPath.rewind()
        val rightX = tapRightStart + slideBarTapWidth
        rightTapPath.moveTo(tapRightStart, 0f)
        rightTapPath.lineTo(tapRightStart, totalHeight)
        rightTapPath.lineTo(rightX - slideBarDiameter.toFloat(), totalHeight)
        rightTapPath.arcTo(
            rightX - slideBarDiameter.toFloat(), totalHeight - slideBarDiameter.toFloat(),
            rightX, totalHeight,
            90f, -90f,
            false
        )
        rightTapPath.lineTo(rightX, slideBarDiameter.toFloat())
        rightTapPath.arcTo(
            rightX - slideBarDiameter.toFloat(), 0f,
            rightX, slideBarDiameter.toFloat(),
            0f, -90f,
            false
        )
        rightTapPath.lineTo(tapRightStart, 0f)
        rightTapPath.close()
        canvas.drawPath(rightTapPath, paint)

        rightTapBlockBitmap?.also {
            val btnWidth = it.width
            val btnHeight = it.height
            canvas.drawBitmap(
                it,
                rightX - btnWidth - (slideBarTapWidth - btnWidth) / 2f,
                (totalHeight - btnHeight) / 2f,
                paint
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val totalHeight = slideBarBorderWidth * 2 + imageHeight
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val rawX = event.x
                val rawY = event.y

                val leftTapRect = RectF(
                    tapLeftStart - slideBarTapWidth, 0f,
                    tapLeftStart, totalHeight
                )
                val rightTapRect = RectF(
                    tapRightStart, 0f,
                    tapRightStart + slideBarTapWidth, totalHeight
                )
                val progressLineRect = RectF(
                    processLineStart - progressLineTapWidth, 0f,
                    processLineStart + progressLineTapWidth, totalHeight
                )

                when {
                    leftTapRect.contains(rawX, rawY) -> {
                        touchProgressLine = false
                        touchLeftBlock = true
                        touchRightBlock = false

                        Log.e(javaClass.simpleName, "触摸了左侧滑块")
                    }
                    rightTapRect.contains(rawX, rawY) -> {
                        touchProgressLine = false
                        touchLeftBlock = false
                        touchRightBlock = true

                        Log.e(javaClass.simpleName, "触摸了右侧滑块")
                    }
                    progressLineRect.contains(rawX, rawY) -> {
                        touchProgressLine = true
                        touchLeftBlock = false
                        touchRightBlock = false

                        Log.e(javaClass.simpleName, "触摸了进度条")
                    }
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                if (touchLeftBlock) {
                    tapLeftStart = limitLeftSliding(x)
                    processLineStart = limitProgressPosition()
                    invalidate()
                }
                if (touchRightBlock) {
                    tapRightStart = limitRightSliding(x)
                    processLineStart = limitProgressPosition()
                    invalidate()
                }

                if (touchProgressLine) {
                    processLineStart = limitProgressLineSliding(x)
                    invalidate()
                }

                // 计算进度相关并回调
                if (touchLeftBlock || touchRightBlock || touchProgressLine) {
                    calculatePosition()
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                touchLeftBlock = false
                touchRightBlock = false
                touchProgressLine = false

                invalidate()
            }
        }

        return super.onTouchEvent(event)
    }


    /**
     * 进度等位置计算
     */
    private fun calculatePosition() {
        val galleryWidth = width - slideBarTapWidth * 2
        // 每个像素代表的时长
        val unit = duration.toFloat() / galleryWidth

        val leftX = tapLeftStart - slideBarTapWidth
        val rightX = tapRightStart - slideBarTapWidth

        val leftPosition = leftX * unit
        val rightPosition = rightX * unit
        val progressPosition = (processLineStart - slideBarTapWidth) * unit
        onCropListener?.onCropChange(
            leftPosition.roundToLong(),
            rightPosition.roundToLong(),
            progressPosition.roundToLong(),
            duration
        )
    }

    /**
     * 限制进度条边界
     */
    private fun limitProgressPosition(): Float {
        if (processLineStart < tapLeftStart) {
            processLineStart = tapLeftStart
        }
        if (processLineStart > tapRightStart) {
            processLineStart = tapRightStart
        }
        return processLineStart
    }

    /**
     * 限制进度条滑动范围
     */
    private fun limitProgressLineSliding(x: Float): Float {
        return when {
            x <= tapLeftStart -> tapLeftStart
            x >= tapRightStart -> tapRightStart
            else -> x
        }
    }

    /**
     * 限制左侧滑动边界
     */
    private fun limitLeftSliding(x: Float): Float {
        return when {
            x <= slideBarTapWidth -> slideBarTapWidth
            x > tapRightStart -> tapRightStart
            else -> x
        }
    }

    /**
     * 限制右侧滑动边界
     */
    private fun limitRightSliding(x: Float): Float {
        return when {
            x <= tapLeftStart -> tapLeftStart
            x > width - slideBarTapWidth -> width - slideBarTapWidth
            else -> x
        }
    }

    interface OnCropListener {

        /**
         * @param startPosition 左侧截取开始时间
         * @param endPosition 右侧截取开始时间
         * @param progressPosition 进度线的时间
         * @param totalDuration 总长时间
         */
        fun onCropChange(
            startPosition: Long,
            endPosition: Long,
            progressPosition: Long,
            totalDuration: Long
        )
    }
}















