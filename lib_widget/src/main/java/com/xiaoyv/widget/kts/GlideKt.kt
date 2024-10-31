package com.xiaoyv.widget.kts

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.xiaoyv.widget.glide.GlideConfig
import okhttp3.OkHttpClient

var requireGlideHttpClient: OkHttpClient = OkHttpClient.Builder().build()

/**
 * 图片加载配置，异常占位等
 */
var requireGlideGlobalConfig = GlideConfig()

/**
 * 加载监听
 */
inline fun <TranscodeType> RequestBuilder<TranscodeType>.listener(
    crossinline onLoadFailed: (Any?) -> Unit = { },
    crossinline onResourceReady: (TranscodeType) -> Unit = { },
): RequestBuilder<TranscodeType> {
    val loadListener = object : RequestListener<TranscodeType> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<TranscodeType>,
            isFirstResource: Boolean
        ): Boolean {
            onLoadFailed.invoke(model)
            return false
        }

        override fun onResourceReady(
            resource: TranscodeType & Any,
            model: Any,
            target: Target<TranscodeType>?,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            onResourceReady.invoke(resource)
            return false
        }
    }
    return addListener(loadListener)
}

/**
 * 加载图片
 */
fun ImageView.loadImage(
    model: Any?,
    cropOrFit: Boolean? = true,
    errorResId: Int? = null,
    placeholderResId: Int? = null,
) {
    if (model == null || context.isDestroyed()) return

    Glide.with(context)
        .load(model)
        .let {
            when (cropOrFit) {
                null -> it
                true -> it.centerCrop()
                else -> it.fitCenter()
            }
        }
        .let {
            when (errorResId) {
                null -> it
                0 -> it.error(requireGlideGlobalConfig.defaultHolder4x3)
                else -> it.error(errorResId)
            }
        }
        .let {
            when (placeholderResId) {
                null -> it
                0 -> it.placeholder(requireGlideGlobalConfig.defaultHolder4x3)
                else -> it.placeholder(placeholderResId)
            }
        }.into(this)
}


fun ImageView.loadAvatar(model: Any? = null) {
    if (model == null || context.isDestroyed()) return

    Glide.with(context)
        .load(model)
        .error(requireGlideGlobalConfig.defaultAvatarError)
        .placeholder(requireGlideGlobalConfig.defaultAvatarHolder)
        .into(this)
}

