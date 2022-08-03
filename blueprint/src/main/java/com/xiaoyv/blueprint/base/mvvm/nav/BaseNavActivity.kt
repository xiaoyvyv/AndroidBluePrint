@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base.mvvm.nav

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.viewbinding.ViewBinding
import com.xiaoyv.blueprint.R
import com.xiaoyv.blueprint.base.BaseActivity
import com.xiaoyv.blueprint.base.createBinding

/**
 * BaseNavActivity
 *
 * NavHostFragment 必须使用 `nav_host_fragment_content` 作为 id
 *
 * Toolbar 设置 id 为 `nav_toolbar` 可以自动适配
 *
 * ```xml
 * <item name="nav_toolbar" type="id" />
 * <item name="nav_host_fragment_content" type="id" />
 * ```
 *
 * @author why
 * @since 2022/7/9
 **/
abstract class BaseNavActivity<VB : ViewBinding> : BaseActivity() {
    lateinit var binding: VB

    /**
     * NavController
     */
    val navController: NavController
        get() = findNavController(R.id.bp_nav_host_fragment_content)

    /**
     * AppBarConfiguration
     */
    lateinit var appBarConfiguration: AppBarConfiguration

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        // 沉浸状态栏
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
    }

    override fun createContentView(): View? {
        binding = createBinding()
        initToolbar()
        return binding.root
    }

    /**
     * Toolbar 适配
     */
    private fun initToolbar() {
        val toolbar = findViewById(R.id.bp_nav_toolbar) ?: onToolbar() ?: return
        setSupportActionBar(toolbar)

        // AppBarConfiguration init
        val topLevelDestinationIds = onTopLevelDestinationIds()
        appBarConfiguration = if (topLevelDestinationIds.isNotEmpty()) {
            AppBarConfiguration(topLevelDestinationIds)
        } else {
            AppBarConfiguration(navController.graph)
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
    }


    /**
     * 若不使用默认 Toolbar Id: nav_toolbar
     * 则检测该返回自定义 Toolbar 配置
     */
    protected open fun onToolbar(): Toolbar? {
        return null
    }

    /**
     * 顶级页面，该页面就不能返回了
     */
    protected open fun onTopLevelDestinationIds(): Set<Int> {
        return emptySet()
    }

    /**
     * initView
     */
    abstract override fun initView()

    /**
     * initData
     */
    abstract override fun initData()
}