package com.xiaoyv.blueprint.main

import com.xiaoyv.blueprint.base.ImplBasePresenter

/**
 * MainPresenter
 *
 * @author why
 * @since 2021/10/9
 */
class MainPresenter : ImplBasePresenter<MainContract.View>(), MainContract.Presenter {
    private val model = MainModel()

    override fun v2pPrint() {
        model.p2mPrint()
    }
}