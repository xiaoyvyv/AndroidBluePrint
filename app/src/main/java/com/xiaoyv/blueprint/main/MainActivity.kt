package com.xiaoyv.blueprint.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.xiaoyv.blueprint.app.databinding.ActivityMainBinding
import com.xiaoyv.blueprint.base.binding.BaseMvpBindingActivity
import com.xiaoyv.blueprint.utils.LazyUtils.loadRootFragment
import com.xiaoyv.widget.dialog.UiOptionsDialog
import com.xiaoyv.widget.utils.ADAPT_WIDTH_DP
import com.xiaoyv.widget.utils.autoConvertDensity
import kotlin.math.max
import kotlin.math.min

class MainActivity :
    BaseMvpBindingActivity<ActivityMainBinding, MainContract.View, MainPresenter>(),
    MainContract.View {

    override fun createPresenter() = MainPresenter()

    override fun createContentBinding(layoutInflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    @SuppressLint("NewApi")
    override fun initView() {
        stateController.fitTitleAndStatusBar = true


//        binding.toolbar.setLeftIcon()
        binding.toolbar.bottomDivider = true
    }

    override fun initData() {
        val mainFragment = MainFragment()
        loadRootFragment(binding.flContainer.id, mainFragment)
    }

    override fun initListener() {
        binding.tvTest.setOnClickListener {
//            stateController.showEmptyView()

            val optionsDialog = UiOptionsDialog.Builder().apply {
                itemDataList = arrayListOf(
                    "横屏",
                    "Toast",
                    "375 - false",
                    "375 - true",
                    "竖屏"
                )
                itemLastColor = Color.RED
                onOptionsClickListener = { dialog, data, position ->
                    when (position) {
                        0 -> {
                            ScreenUtils.setLandscape(this@MainActivity)
                        }
                        1 -> {
                            val screenWidth = ScreenUtils.getScreenWidth()
                            val screenHeight = ScreenUtils.getScreenHeight()
                            val min = min(screenWidth, screenHeight)
                            val max = max(screenWidth, screenHeight)
                            val adaptHeight = max * ADAPT_WIDTH_DP / min

                            ToastUtils.showLong(" adaptHeight: $adaptHeight")
                        }
                        2 -> {
                            val screenWidth = ScreenUtils.getAppScreenWidth()
                            val screenHeight = ScreenUtils.getAppScreenHeight()
                            val min = min(screenWidth, screenHeight)
                            val max = max(screenWidth, screenHeight)
                            val adaptHeight = max * ADAPT_WIDTH_DP / min

                        }
                        3 -> {

                        }
                        else -> {
                            ScreenUtils.setPortrait(this@MainActivity)
                        }
                    }
                    true
                }
            }.create()

            optionsDialog.show(this)


//            val create = UiNormalDialog.Builder()
//                .apply {
////                    customView = R.layout.ui_view_list_no_more
////                    customViewInitListener = {
////                        val bind = UiViewListNoMoreBinding.bind(it)
////                        bind.tvAbnormal.text="ssssssssss"
////                    }
//                }.create()
//            create.show(this)
        }
    }

    override fun onPresenterCreated() {
        presenter.v2pPrint()
    }

    override fun p2vShowInfo() {

    }
}