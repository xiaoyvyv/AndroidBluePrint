@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.xiaoyv.widget.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import com.blankj.utilcode.util.BarUtils
import com.xiaoyv.widget.databinding.UiViewToolbarLargeBinding

/**
 * UiLargeToolbar
 *
 * @author why
 * @since 2022/1/18
 */
class UiLargeToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {
    private val binding = UiViewToolbarLargeBinding.inflate(LayoutInflater.from(context), this)

    var title: String? = ""
        set(value) {
            field = value
            binding.tvTitle.text = value
        }

    @DrawableRes
    var icon: Int = 0
        set(value) {
            field = value
            binding.ivIcon.setImageResource(value)
        }

    /**
     * 是否自动填充状态栏高度
     */
    var fitStatusBar: Boolean = true
        set(value) {
            field = value
            if (value) {
                updatePadding(top = BarUtils.getStatusBarHeight())
            } else {
                updatePadding(top = 0)
            }
        }

    var onIconClickListener: () -> Unit = {}
        set(value) {
            field = value
            binding.ivIcon.setOnClickListener {
                value.invoke()
            }
        }

    init {
        fitStatusBar = true
    }

}