package com.xiaoyv.blueprint.activity

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel

/**
 * MvvmTest
 *
 * @author why
 * @since 2022/8/4
 **/
class MvvmTestViewModel : BaseViewModel() {

    val helloObserver = MutableLiveData<String>()


    fun changeTip() {
        helloObserver.value = "Hello why!"
    }
}