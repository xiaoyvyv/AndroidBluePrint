package com.xiaoyv.blueprint.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import com.blankj.utilcode.util.BarUtils
import com.xiaoyv.blueprint.app.databinding.ActivityMainBinding
import com.xiaoyv.blueprint.base.binding.BaseMvpBindingActivity
import com.xiaoyv.blueprint.utils.LazyUtils.loadRootFragment
import com.xiaoyv.widget.dialog.UiNormalDialog
import com.xiaoyv.widget.utils.doOnBarClick

class MainActivity :
    BaseMvpBindingActivity<ActivityMainBinding, MainContract.View, MainPresenter>(),
    MainContract.View {

    override fun createPresenter() = MainPresenter()

    override fun createContentBinding(layoutInflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    @SuppressLint("NewApi")
    override fun initView() {
        stateController.topSpaceToViewBottom = binding.toolbar
        stateController.showEmptyView()

        binding.toolbar.setLeftIcon()
        binding.toolbar.bottomDivider = true
    }

    override fun initData() {
        val mainFragment = MainFragment()
        loadRootFragment(binding.flContainer.id, mainFragment)
    }

    override fun initListener() {
        binding.tvTest.setOnClickListener {
            val create = UiNormalDialog.Builder()
                .apply {
//                    customView = R.layout.ui_view_list_no_more
//                    customViewInitListener = {
//                        val bind = UiViewListNoMoreBinding.bind(it)
//                        bind.tvAbnormal.text="ssssssssss"
//                    }
                }.create()
            create.show(this)
        }
    }

    override fun onPresenterCreated() {
        presenter.v2pPrint()
    }

    override fun p2vShowInfo() {

    }
}