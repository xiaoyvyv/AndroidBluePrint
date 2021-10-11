package com.xiaoyv.blueprint.main

import com.blankj.utilcode.util.LogUtils

/**
 * MainModel
 *
 * @author why
 * @since 2021/10/9
 */
class MainModel : MainContract.Model {
    override fun p2mPrint() {
        LogUtils.i("MainModel Print!!!")
    }

}