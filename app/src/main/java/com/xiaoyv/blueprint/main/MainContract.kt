package com.xiaoyv.blueprint.main

import com.blankj.utilcode.util.LogUtils
import com.xiaoyv.blueprint.base.IBaseContract
import com.xiaoyv.blueprint.base.IBaseView

/**
 * MainContract
 *
 * @author why
 * @since 2021/10/9
 */
interface MainContract {
    interface View :IBaseView {
        fun p2vShowInfo()
    }

    interface Presenter : IBaseContract.Presenter {
        fun v2pPrint()
    }

    interface Model : IBaseContract.Model {
        fun p2mPrint()
    }
}