@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.widget.glide

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.xiaoyv.widget.utils.isDestroyed
import okhttp3.OkHttpClient

/**
 * GlideHelper 图片加载
 *
 * @author why
 * @since 2021/12/13
 */
object GlideHelper {
    var requireHttpClient: OkHttpClient = OkHttpClient.Builder().build()

    /**
     * 图片加载配置，异常占位等
     */
    var globalConfig: GlideConfig = GlideConfig.DEFAULT_CONFIG


    @JvmStatic
    fun ImageView.loadAvatar(context: Context, model: Any) {
        if (context.isDestroyed()) {
            return
        }

        Glide.with(context)
            .load(model)
            .error(globalConfig.defaultAvatarError)
            .placeholder(globalConfig.defaultAvatarHolder)
            .into(this)
    }

    @JvmStatic
    fun ImageView.loadImage(context: Context, model: Any?, cropOrFit: Boolean? = true) {
        if (context.isDestroyed()) {
            return
        }

        Glide.with(context)
            .load(model ?: return)
            .let {
                if (cropOrFit != null) {
                    return@let if (cropOrFit) it.centerCrop() else it.fitCenter()
                }
                return@let it
            }
            .error(globalConfig.defaultHolder4_3)
            .placeholder(globalConfig.defaultHolder4_3)
            .into(this)
    }

}