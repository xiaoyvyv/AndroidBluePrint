package com.xiaoyv.blueprint.main

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
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

    override fun createContentView(inflater: LayoutInflater, flRoot: FrameLayout): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        binding.tvTest.setOnClickListener {
            LocalizeManager.switchLanguage(LanguageType.LANGUAGE_ZH_HANS)
        }
    }

    override fun initData() {

    }
}