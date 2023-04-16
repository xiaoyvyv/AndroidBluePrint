@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.xiaoyv.floater.widget.indicator

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ColorUtils
import com.xiaoyv.floater.widget.R
import com.xiaoyv.floater.widget.kts.getDpx
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import java.io.Serializable

/**
 * UiCommonIndicator
 *
 * @author why
 * @since 2022/1/19
 */
class UiCommonIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : MagicIndicator(context, attrs) {

    var config = Config(isInEditMode)

    /**
     * 指示器数据
     */
    private var titles: List<String> = arrayListOf()
    private var viewPager2: ViewPager2? = null

    /**
     * 指示器数据适配器
     */
    private val commonNavigatorAdapter = UiCommonIndicatorAdapter()

    /**
     * 公用类型指示器
     */
    private val commonNavigator = CommonNavigator(context).also {
        it.leftPadding = getDpx(4f)
        it.rightPadding = getDpx(4f)
        it.isEnablePivotScroll = true
        it.isAdjustMode = false
    }


    fun initTitleAndPager(titles: List<String>, viewPager2: ViewPager2) {
        this.titles = titles
        this.viewPager2 = viewPager2

        bindIndicatorWithViewPager(viewPager2)

        commonNavigator.adapter = commonNavigatorAdapter
        navigator = commonNavigator

        refreshIndicator()
    }

    /**
     * ViewPager 和 Indicator 绑定
     *
     * @param viewPager2 ViewPager2
     */
    private fun bindIndicatorWithViewPager(viewPager2: ViewPager2) {
        setBackgroundColor(Color.TRANSPARENT)
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                this@UiCommonIndicator.onPageScrolled(
                    position,
                    positionOffset,
                    positionOffsetPixels
                )
            }

            override fun onPageSelected(position: Int) {
                this@UiCommonIndicator.onPageSelected(position)
                viewPager2.requestLayout()
            }

            override fun onPageScrollStateChanged(state: Int) {
                this@UiCommonIndicator.onPageScrollStateChanged(state)
            }
        })
    }

    /**
     * 刷新指示器
     */
    fun refreshIndicator() {
        commonNavigatorAdapter.notifyDataSetChanged()
    }

    /**
     * 适配器实现类
     */
    inner class UiCommonIndicatorAdapter : CommonNavigatorAdapter() {

        override fun getCount(): Int {
            return titles.size
        }

        override fun getTitleView(context: Context, index: Int): IPagerTitleView {
            return UiIndicatorTitleView(context).apply {
                selectedColor = config.selectTextColor
                normalColor = config.normalTextColor
                text = titles[index]
                setOnClickListener {
                    viewPager2?.currentItem = index
                }
            }
        }

        override fun getIndicator(context: Context): IPagerIndicator? {
            if (config.showIndicator) {
                return LinePagerIndicator(context).apply {
                    setColors(config.indicatorColor)
                    mode = LinePagerIndicator.MODE_WRAP_CONTENT
                    lineHeight = getDpx(3f).toFloat()
                    roundRadius = 0f
                }
            }
            return null
        }
    }

    /**
     * 指示器 Tab 控件
     */
    inner class UiIndicatorTitleView(context: Context) : ColorTransitionPagerTitleView(context) {

        init {
            gravity = Gravity.CENTER_VERTICAL
            textSize = config.selectTextSize
            typeface = Typeface.DEFAULT_BOLD
            setPadding(getDpx(12f), 0, getDpx(12f), 0)
        }

        override fun onSelected(index: Int, totalCount: Int) {
            textSize = config.selectTextSize
            typeface = if (config.selectTextBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }

        override fun onDeselected(index: Int, totalCount: Int) {
            textSize = config.normalTextSize
            typeface = if (config.normalTextBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }
    }

    /**
     * 配置
     */
    data class Config(
        private val isInEditMode: Boolean = true,
        var selectTextSize: Float = 18f,
        @ColorInt var selectTextColor: Int = if (isInEditMode) Color.BLACK else ColorUtils.getColor(
            R.color.ui_text_c1
        ),
        var selectTextBold: Boolean = true,
        var normalTextSize: Float = 16f,
        var normalTextColor: Int = if (isInEditMode) Color.BLACK else ColorUtils.getColor(R.color.ui_text_c2),
        var normalTextBold: Boolean = false,
        @ColorInt var indicatorColor: Int = if (isInEditMode) Color.CYAN else ColorUtils.getColor(R.color.ui_theme_c0),
        var showIndicator: Boolean = true,
    ) : Serializable

}