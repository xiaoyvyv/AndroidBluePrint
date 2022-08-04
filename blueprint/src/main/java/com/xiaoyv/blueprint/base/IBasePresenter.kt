package com.xiaoyv.blueprint.base

import androidx.lifecycle.*

/**
 * BasePresenter
 *
 * @author why
 * @since 2020/11/28
 */
interface IBasePresenter : DefaultLifecycleObserver {

    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner)
}