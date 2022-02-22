package com.xiaoyv.widget.glide

import androidx.annotation.DrawableRes
import com.xiaoyv.widget.R

/**
 * GlideConfig
 *
 * @author why
 * @since 2021/12/13
 */
data class GlideConfig(
    /**
     * 头像加载中、或失败|默认
     */
    @DrawableRes var defaultAvatarHolder: Int = R.drawable.ui_pic_avatar_loading,
    @DrawableRes var defaultAvatarError: Int = R.drawable.ui_pic_avatar_default,

    @DrawableRes var defaultHolder1_1: Int = 0,
    @DrawableRes var defaultHolder4_3: Int = 0,
    @DrawableRes var defaultHolder16_9: Int = 0,

) {
    companion object {
        @JvmStatic
        val DEFAULT_CONFIG: GlideConfig
            get() = GlideConfig()
    }
}