package com.xiaoyv.blueprint.main

import android.view.LayoutInflater
import com.xiaoyv.blueprint.app.R
import com.xiaoyv.blueprint.app.databinding.ActivityMainBinding
import com.xiaoyv.blueprint.base.binding.BaseMvpBindingActivity
import com.xiaoyv.blueprint.utils.LazyUtils.loadRootFragment
import com.xiaoyv.widget.databinding.UiViewListNoMoreBinding
import com.xiaoyv.widget.dialog.UiNormalDialog

class MainActivity :
    BaseMvpBindingActivity<ActivityMainBinding, MainContract.View, MainPresenter>(),
    MainContract.View {

    override fun createPresenter() = MainPresenter()

    override fun createContentBinding(layoutInflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {

    }

    override fun initData() {
        val mainFragment = MainFragment()
        loadRootFragment(binding.flContainer.id, mainFragment)
    }

    override fun initListener() {
        binding.tvTest.setOnClickListener {
            val create = UiNormalDialog.Builder()
                .apply {
                    customView = R.layout.ui_view_list_no_more
                    customViewInitListener = {
                        val bind = UiViewListNoMoreBinding.bind(it)
                        bind.tvAbnormal.text="ssssssssss"
                    }
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