package com.xiaoyv.blueprint.main

import android.view.View
import com.xiaoyv.blueprint.app.databinding.FragmentMainBinding
import com.xiaoyv.blueprint.base.BaseFragment
import com.xiaoyv.blueprint.localize.LanguageType
import com.xiaoyv.blueprint.localize.LocalizeManager

/**
 * MainFragment
 *
 * @author why
 * @since 2022/2/22
 */
class MainFragment : BaseFragment() {

    private lateinit var binding: FragmentMainBinding

    override fun createContentView(): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        binding.tvTest.setOnClickListener {
            LocalizeManager.switchLanguage(LanguageType.LANGUAGE_EN)
        }
    }

    override fun initData() {

    }
}