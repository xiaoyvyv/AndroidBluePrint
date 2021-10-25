package com.xiaoyv.blueprint

import android.view.View
import com.xiaoyv.blueprint.app.databinding.FragmentMainBinding
import com.xiaoyv.blueprint.base.BaseFragment

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