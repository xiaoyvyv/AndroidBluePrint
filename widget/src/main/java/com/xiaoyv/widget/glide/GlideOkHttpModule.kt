@file:Suppress("unused")

package com.xiaoyv.widget.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.LibraryGlideModule
import com.xiaoyv.widget.kts.requireGlideHttpClient
import java.io.InputStream


/**
 * OkHttpGlideModule
 *
 * @author why
 * @since 2021/12/13
 */
@GlideModule
open class GlideOkHttpModule : LibraryGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(requireGlideHttpClient)
        )
    }
}