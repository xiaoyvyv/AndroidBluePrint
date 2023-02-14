package com.xiaoyv.blueprint.main

import com.xiaoyv.blueprint.app.databinding.FragmentMainBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingFragment
import com.xiaoyv.blueprint.localize.LanguageType
import com.xiaoyv.blueprint.localize.LocalizeManager

/**
 * MainFragment
 *
 * @author why
 * @since 2022/2/22
 */
class MainFragment : BaseBindingFragment<FragmentMainBinding>() {

    override fun initView() {
        binding.tvTest.setOnClickListener {
            LocalizeManager.switchLanguage(LanguageType.LANGUAGE_ZH_HANS)
        }
    }

    override fun initData() {

    }
}