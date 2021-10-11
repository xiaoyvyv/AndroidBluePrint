package com.xiaoyv.blueprint.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * BasePresenter
 *
 * @author why
 * @since 2020/11/28
 */
interface IBasePresenter : LifecycleObserver {

    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner)

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun v2pOnCreate() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun v2pOnStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun v2pOnResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun v2pOnPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun v2pOnStop() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun v2pOnDestroy() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onLifecycleChanged(owner: LifecycleOwner, event: Lifecycle.Event) {
    }

}