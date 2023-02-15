package com.xiaoyv.blueprint.base.binding

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.xiaoyv.blueprint.base.BaseActivity
import com.xiaoyv.widget.utils.injectViewBinding

/**
 * BaseBindingActivity
 *
 * @author why
 * @since 2021/10/9
 */
abstract class BaseBindingActivity<BINDING : ViewBinding> : BaseActivity() {
    lateinit var binding: BINDING

    @CallSuper
    override fun createContentView(): View {
        binding = createContentBinding(layoutInflater)
        return binding.root
    }

    protected open fun createContentBinding(layoutInflater: LayoutInflater): BINDING {
        return injectViewBinding()
    }

    abstract override fun initView()

    abstract override fun initData()
}