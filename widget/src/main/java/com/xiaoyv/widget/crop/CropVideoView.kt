@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.widget.crop

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.xiaoyv.widget.utils.getDpx

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
    var slideBarDiameter = getDpx(16f)

    /**
     * 上下边框高度
     */
    var slideBarBorderWidth: Float = getDpx(4f).toFloat()

    /**
     * 拖拽栏，左右两块触摸位置宽度
     */
    var slideBarTapWidth: Float = getDpx(20f).toFloat()

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
    var processLineColor = Color.WHITE

    /**
     * 进度竖线宽度
     */
    var processLineWidth = getDpx(4f)

    /**
     * 进度竖线圆角
     */
    var processLineRadius = getDpx(4f)

    /**
     * 图片高度
     */
    var imageHeight = getDpx(60f)

    /**
     * 时长
     */
    var duration: Long = 0

    /**
     * 左右触摸块的相对中心方向的边界位置
     */
    private var tapLeftStart: Float = (slideBarTapWidth * 3)
    private var tapRightStart: Float = (slideBarTapWidth * 8)

    private var touchLeftBlock = false
    private var touchRightBlock = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackgroundBar(canvas)
        drawSlideBar(canvas)
    }

    private val bgPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isDither = true
            strokeWidth = (slideBarBorderWidth * 2 + imageHeight).toFloat()
            style = Paint.Style.FILL_AND_STROKE
            color = backgroundBarColor
        }
    }

    /**
     * 绘制背景栏
     */
    private fun drawBackgroundBar(canvas: Canvas) {
        val totalHeight = slideBarBorderWidth * 2 + imageHeight

        bgPaint.strokeWidth = 0f
        bgPaint.style = Paint.Style.FILL
        bgPaint.color = backgroundBarColor

        val bgRect = RectF(0f, 0f, width.toFloat(), totalHeight)
        canvas.drawRoundRect(bgRect, slideBarDiameter.toFloat() / 2f, slideBarDiameter.toFloat() / 2f, bgPaint)

        bgPaint.color = Color.DKGRAY
        canvas.drawRect(
            slideBarTapWidth,
            slideBarBorderWidth.toFloat(),
            (width.toFloat() - slideBarTapWidth),
            (totalHeight - slideBarBorderWidth),
            bgPaint
        )
    }

    /**
     * 绘制背景栏
     */
    private fun drawSlideBar(canvas: Canvas) {
        val totalHeight = slideBarBorderWidth * 2 + imageHeight

        bgPaint.color = slideBarColor
        bgPaint.style = Paint.Style.FILL
        bgPaint.strokeWidth = slideBarBorderWidth.toFloat()
        canvas.drawLine(
            tapLeftStart,
            slideBarBorderWidth / 2f,
            tapRightStart,
            slideBarBorderWidth / 2f,
            bgPaint
        )
        canvas.drawLine(
            tapLeftStart,
            totalHeight - slideBarBorderWidth / 2f,
            tapRightStart,
            totalHeight - slideBarBorderWidth / 2f,
            bgPaint
        )

        // 左侧 TAP 按钮
        val leftTapPath = Path()
        val leftX = tapLeftStart - slideBarTapWidth
        leftTapPath.moveTo(tapLeftStart, 0f)
        leftTapPath.lineTo(tapLeftStart, totalHeight)
        leftTapPath.lineTo(leftX + slideBarDiameter.toFloat(), totalHeight)
        leftTapPath.arcTo(leftX, totalHeight - slideBarDiameter.toFloat(), leftX + slideBarDiameter.toFloat(), totalHeight, 90f, 90f, false)
        leftTapPath.lineTo(leftX, slideBarDiameter.toFloat())
        leftTapPath.arcTo(leftX, 0f, leftX + slideBarDiameter.toFloat(), slideBarDiameter.toFloat(), 180f, 90f, false)
        leftTapPath.lineTo(leftX + slideBarDiameter.toFloat(), 0f)
        leftTapPath.close()
        canvas.drawPath(leftTapPath, bgPaint)


        // 右侧 TAP 按钮
        val rightTapPath = Path()
        rightTapPath.rewind()
        val rightX = tapRightStart + slideBarTapWidth
        rightTapPath.moveTo(tapRightStart, 0f)
        rightTapPath.lineTo(tapRightStart, totalHeight)
        rightTapPath.lineTo(rightX - slideBarDiameter.toFloat(), totalHeight)
        rightTapPath.arcTo(
            rightX - slideBarDiameter.toFloat(),
            totalHeight - slideBarDiameter.toFloat(),
            rightX,
            totalHeight,
            90f,
            -90f,
            false
        )
        rightTapPath.lineTo(rightX, slideBarDiameter.toFloat())
        rightTapPath.arcTo(rightX - slideBarDiameter.toFloat(), 0f, rightX, slideBarDiameter.toFloat(), 0f, -90f, false)
        rightTapPath.lineTo(tapRightStart, 0f)
        rightTapPath.close()
        canvas.drawPath(rightTapPath, bgPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val totalHeight = slideBarBorderWidth * 2 + imageHeight
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val rawX = event.x
                val rawY = event.y

                val leftTapRect = RectF(tapLeftStart - slideBarTapWidth, 0f, tapLeftStart, totalHeight)
                val rightTapRect = RectF(tapRightStart, 0f, tapRightStart + slideBarTapWidth, totalHeight)

                if (leftTapRect.contains(rawX, rawY)) {
                    touchLeftBlock = true
                    Log.e(javaClass.simpleName, "触摸了左侧滑块")
                }

                if (rightTapRect.contains(rawX, rawY)) {
                    touchRightBlock = true
                    Log.e(javaClass.simpleName, "触摸了右侧滑块")
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val x = event.x
                if (touchLeftBlock) {
                    tapLeftStart = limitLeftSliding(x)
                    invalidate()
                }
                if (touchRightBlock) {
                    tapRightStart =limitRightSliding(x)
                    invalidate()
                }
                Log.e(javaClass.simpleName, "onTouchEvent: $x")
                return true
            }
            MotionEvent.ACTION_UP -> {
                touchLeftBlock = false
                touchRightBlock = false
            }
        }

        return super.onTouchEvent(event)
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
}















