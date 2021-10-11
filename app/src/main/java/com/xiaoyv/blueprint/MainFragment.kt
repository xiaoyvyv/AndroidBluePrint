package com.xiaoyv.blueprint

import android.view.View
import com.github.nukc.stateview.StateView
import com.xiaoyv.blueprint.base.BaseFragment
import com.xiaoyv.blueprint.databinding.FragmentMainBinding

/**
 * MainFragment
 *
 * @author why
 * @since 2021/10/9
 */
class MainFragment : BaseFragment() {
    override fun createContentView(): View {
        val inflate = FragmentMainBinding.inflate(layoutInflater)
        return inflate.root
    }

    override fun initView() {

    }

    override fun initData() {
//        p2vShowLoadingView()
    }

}