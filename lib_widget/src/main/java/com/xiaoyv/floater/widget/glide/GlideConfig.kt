package com.xiaoyv.floater.widget.glide

import androidx.annotation.DrawableRes
import com.xiaoyv.floater.widget.R

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
    @DrawableRes
    var defaultAvatarHolder: Int = R.drawable.ui_pic_avatar_loading,
    @DrawableRes
    var defaultAvatarError: Int = R.drawable.ui_pic_avatar_default,

    @DrawableRes
    var defaultHolder1x1: Int = 0,
    @DrawableRes
    var defaultHolder4x3: Int = 0,
    @DrawableRes
    var defaultHolder16x9: Int = 0
)