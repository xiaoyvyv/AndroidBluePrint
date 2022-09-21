@file:Suppress("MemberVisibilityCanBePrivate")

package com.xiaoyv.blueprint.base.mvvm.nav

import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel


/**
 * BaseNavViewModel
 *
 * @author why
 * @since 2022/7/9
 **/
open class BaseNavViewModel : BaseViewModel() {
    open fun onAnimationStart() {}

    open fun onAnimationEnd() {}
}