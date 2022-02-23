@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.xiaoyv.widget.stateview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.StringUtils
import com.github.nukc.stateview.StateView
import com.xiaoyv.widget.R
import com.xiaoyv.widget.databinding.UiViewStatusErrorBinding
import com.xiaoyv.widget.databinding.UiViewStatusTipBinding

/**
 * StateViewImpl 全局状态布局
 *
 * @author why
 * @since 2021/10/15
 */
abstract class StateViewImpl(private val activity: FragmentActivity) : IStateView {

    /**
     * 获取状态布局
     */
    private val requireStateView: StateView
        get() = onCreateStateView()

    /**
     * 若有图片 id=R.id.iv_status ，设置在屏幕中的偏移系数，控制位置比例，默认 0.3
     */
    var imageVerticalBias = 0.3f

    /**
     * 顶部间距
     */
    var topSpaceHeight = 0

    /**
     * 设置空白高度到某个 View 的底部
     */
    var topSpaceToViewBottom: View? = null

    /**
     * 获取顶部间隔
     */
    private val requireTopSpace: Int
        get() {
            val view = topSpaceToViewBottom ?: return topSpaceHeight
            val positionArray = IntArray(2).also {
                view.getLocationInWindow(it)
            }
            topSpaceHeight = positionArray[1] + view.height
            return topSpaceHeight
        }

    override fun showNormalView() {
        if (checkDestroy()) return
        requireStateView.showContent()
        requireStateView.adjustLocation()
    }

    /**
     * 加载中
     */
    override fun showLoadingView(): View? {
        if (checkDestroy()) return null
        return requireStateView.showLoading()
            .adjustLocation()
            .adjustIvStatusBias()
    }

    /**
     * 空布局
     */
    override fun showEmptyView(): View? {
        if (checkDestroy()) return null
        return requireStateView.showEmpty()
            .adjustIvStatusBias()
            .adjustLocation()
    }


    /**
     * 提示布局（可自定义文字、状态图片）
     */
    override fun showTipView(msg: String?, @DrawableRes imgResId: Int?): View? {
        if (checkDestroy()) return null
        return requireStateView.showEmpty().also {
            val stateBinding = UiViewStatusTipBinding.bind(it)

            stateBinding.tvHint.text = msg
                ?: StringUtils.getString(R.string.ui_view_status_empty)

            stateBinding.ivStatus.setImageResource(
                imgResId ?: R.drawable.ui_pic_status_empty_normal
            )
        }.adjustIvStatusBias()
            .adjustLocation()
    }

    /**
     * 带按钮的布局（可自定义文字、按钮文字、状态图片）
     */
    override fun showRetryView(msg: String?, btText: String?, @DrawableRes imgResId: Int?): View? {
        if (checkDestroy()) return null
        return requireStateView.showRetry().also {
            val stateBinding = UiViewStatusErrorBinding.bind(it)
            stateBinding.tvHint.text = msg
                ?: getDefaultNetErrorMsg()

            stateBinding.tvOperate.text = btText
                ?: StringUtils.getString(R.string.ui_view_status_retry)

            stateBinding.ivStatus.setImageResource(
                imgResId ?: R.drawable.ui_pic_status_empty_normal
            )
        }.adjustIvStatusBias()
            .adjustLocation()
    }

    private fun checkDestroy(): Boolean {
        return activity.isFinishing || activity.isDestroyed
    }

    abstract override fun onCreateStateView(): StateView

    /**
     * 调整图片布局，默认垂直偏移 30%
     */
    private fun View.adjustIvStatusBias(): View {
        LogUtils.i("设置偏移, value:$imageVerticalBias")
        if (this !is ViewGroup) {
            return this
        }
        val imageView: AppCompatImageView = findViewById(R.id.iv_status) ?: return this
        if (imageView.layoutParams is ConstraintLayout.LayoutParams) {
            imageView.updateLayoutParams<ConstraintLayout.LayoutParams> {
                verticalBias = imageVerticalBias
            }
        }
        return this
    }

    /**
     * 调整 位置相关
     */
    private fun View.adjustLocation(): View {
        LogUtils.i("设置 Margin, value:$requireTopSpace")
        if (this !is ViewGroup) {
            return this
        }
        doOnPreDraw {
            updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = requireTopSpace
            }
        }
        return this
    }


    companion object {

        /**
         * 创建状态布局
         *
         * @param context context
         * @param retryAction 带按钮的布局、按钮点击事件
         */
        @JvmStatic
        @JvmOverloads
        fun createStateView(
            context: Context,
            parent: FrameLayout,
            retryAction: (StateView, View) -> Unit = { _, _ -> },
        ) = StateView(context).also {
            it.emptyResource = R.layout.ui_view_status_tip
            it.loadingResource = R.layout.ui_view_status_loading
            it.retryResource = R.layout.ui_view_status_error
            it.onInflateListener = object : StateView.OnInflateListener {
                override fun onInflate(layoutResource: Int, view: View) {
                    if (layoutResource == it.retryResource) {
                        val retryBtn = view.findViewById<View>(R.id.tv_operate) ?: return
                        // 点击重试
                        retryBtn.setOnClickListener { v ->
                            retryAction.invoke(it, v)
                        }
                    }
                }
            }

            // 添加到父布局
            parent.addView(
                it, FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        }

        /**
         * 获取默认网络错误文案
         */
        @JvmStatic
        fun getDefaultNetErrorMsg(): String =
            when {
                NetworkUtils.isConnected() -> {
                    StringUtils.getString(R.string.ui_common_timeout)
                }
                else -> {
                    StringUtils.getString(R.string.ui_common_error_network)
                }
            }
    }
}