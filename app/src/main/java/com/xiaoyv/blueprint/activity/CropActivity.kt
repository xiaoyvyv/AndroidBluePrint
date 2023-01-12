package com.xiaoyv.blueprint.activity

import android.view.View
import com.xiaoyv.blueprint.app.databinding.ActivityCropBinding
import com.xiaoyv.blueprint.base.BaseActivity

/**
 * CropActivity
 *
 * @author why
 * @since 2023/1/12
 **/
class CropActivity : BaseActivity() {
    private val binding by lazy { ActivityCropBinding.inflate(layoutInflater) }

    override fun createContentView(): View {
        return binding.root
    }

    override fun initView() {

    }

    override fun initData() {

    }

}