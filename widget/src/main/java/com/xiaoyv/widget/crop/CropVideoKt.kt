package com.xiaoyv.widget.crop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import kotlin.math.min

/**
 * 避免直接 BitmapFactory.decodeResource 加载原尺寸的采样率导致 OOM
 * 这里先计算合适的采样率，再 decodeResource 出 Bitmap 并缩放至目标大小
 */
fun decodeResourceWithWH(context: Context, resId: Int, width: Int, height: Int): Bitmap? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeResource(context.resources, resId, options)

    val originWidth = options.outWidth
    val originHeight = options.outHeight

    // 目标图片的长度和宽度，这两个值可以是手机的屏幕大小，或者是显示bitmap的ImageView的大小
    val realWidth = width.toFloat()
    val realHeight = height.toFloat()

    // 默认不进行缩放
    var sampleRatio = 1
    val widthRatio: Float
    val heightRatio: Float

    // 分别计算宽高的比率，然后从中取最小值，注意分子和分母的位置，采样率最后是取倒数的，因此使用大值/小值的方法
    if (originWidth > realWidth || originHeight > realHeight) {
        widthRatio = (originWidth / realWidth)
        heightRatio = (originHeight / realHeight)
        sampleRatio = min(widthRatio, heightRatio).toInt()
    }

    // 通过options来修改采样率，进而缩小图片
    options.inSampleSize = sampleRatio
    options.inJustDecodeBounds = false
    return BitmapFactory.decodeResource(context.resources, resId, options).let {
        Bitmap.createScaledBitmap(it, width, height, true)
    }
}

/**
 * 抽取视频帧
 */
suspend fun extractFrames(
    videoFile: String,
    frameAtTime: List<Long>,
    targetSize: Int = 300
): List<Bitmap> {
    val bitmaps = arrayListOf<Bitmap?>()

    runCatching {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoFile)

        // 视频文件时长
        val videoDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            .orEmpty().toLongOrNull() ?: 0

        // 抽取帧
        frameAtTime.onEach {
            if (it > videoDuration || it < 0) {
                return@onEach
            }
            val time = System.currentTimeMillis()
            val frameBitmap =
                retriever.getFrameAtTime(it * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            if (frameBitmap != null && frameBitmap.width > 0 && frameBitmap.height > 0) {
                val width = frameBitmap.width
                val height = frameBitmap.height
                val squareBitmap =
                    Bitmap.createBitmap(frameBitmap, 0, 0, min(width, height), min(width, height))
                if (targetSize > 0) {
                    bitmaps.add(
                        Bitmap.createScaledBitmap(
                            squareBitmap,
                            targetSize,
                            targetSize,
                            true
                        )
                    )
                } else {
                    bitmaps.add(frameBitmap)
                }
            }
            runCatching { frameBitmap?.recycle() }
            val spend = System.currentTimeMillis() - time
            Log.i("CropVideoFrame", "extractFrames: $spend ms")
        }
    }
    return bitmaps.filterNotNull()
}
