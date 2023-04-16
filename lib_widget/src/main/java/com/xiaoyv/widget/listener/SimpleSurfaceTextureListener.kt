package com.xiaoyv.widget.listener

import android.graphics.SurfaceTexture
import android.view.TextureView

/**
 * SimpleSurfaceTextureListener
 *
 * @author why
 * @since 2023/3/12
 */
interface SimpleSurfaceTextureListener : TextureView.SurfaceTextureListener {
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }
}