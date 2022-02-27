package com.xiaoyv.blueprint.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.nukc.stateview.StateView
import com.xiaoyv.blueprint.app.R
import com.xiaoyv.blueprint.app.databinding.ActivityMainBinding
import com.xiaoyv.blueprint.app.databinding.DialogInputBinding
import com.xiaoyv.blueprint.base.binding.BaseMvpBindingActivity
import com.xiaoyv.blueprint.utils.LazyUtils.loadRootFragment
import com.xiaoyv.widget.dialog.UiNormalDialog
import com.xiaoyv.widget.dialog.UiOptionsDialog
import com.xiaoyv.widget.utils.AdaptCompat.ADAPT_WIDTH_DP
import com.xiaoyv.widget.utils.isSoftInputModeAlwaysVisible
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
//            stateController.showRetryView()

            val optionsDialog = UiOptionsDialog.Builder().apply {
                itemDataList = arrayListOf(
                    "横屏",
                    "Toast",
                    "Setting",
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
                            dialog.dismiss()

                            UiNormalDialog.Builder().apply {
                                customView = R.layout.dialog_input
                                onDismissListener = {
                                    LogUtils.e("onDismissListener")
                                }
                                onShowListener = {
                                    LogUtils.e("onShowListener")
                                    val inputBinding = DialogInputBinding.bind(it.requireCustomView)
                                    KeyboardUtils.showSoftInput(inputBinding.tvInput)
//
//                                    inputBinding.tvInput.postDelayed({
//                                        KeyboardUtils.showSoftInput(inputBinding.tvInput)
//                                    }, 200)
                                }
                                onStartListener = { dialog, window ->
                                    LogUtils.e("onStartListener")
                                    window.isSoftInputModeAlwaysVisible = true
                                }
                            }.create()
                                .show(this@MainActivity)
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
        }
    }

    override fun onPresenterCreated() {
        presenter.v2pPrint()
    }

    override fun p2vShowInfo() {

    }

    override fun p2vClickStatusView(stateView: StateView, view: View) {

    }
}