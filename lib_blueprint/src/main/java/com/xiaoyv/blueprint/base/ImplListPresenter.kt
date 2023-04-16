package com.xiaoyv.blueprint.base

import com.blankj.utilcode.util.CollectionUtils

/**
 * ImplListPresenter
 *
 * @author why
 * @since 2021/09/22
 **/
abstract class ImplListPresenter<T, V : IBaseView> : ImplBasePresenter<V>() {

    var current = 1

    open fun v2pIsRefresh(): Boolean = current == 1

    open fun v2pIsLoadMore(): Boolean = !v2pIsRefresh()

    open fun v2pRefresh() {
        current = 1
        loadData()
    }

    open fun v2pLoadMore() {
        current++
        loadData()
    }

    abstract fun loadData()

    /**
     * 处理响应成功的数据
     */
    open fun dealSuccessData(dataList: List<T>?) {
        if (CollectionUtils.isEmpty(dataList)) {
            dealDataEmpty()
        } else {
            p2vFinishRefresh()
            p2vShowList(v2pIsRefresh(), dataList!!)

            requireView.stateController.showNormalView()
        }
    }

    /**
     * 处理响应失败的数据
     */
    open fun dealErrorData(exception: Exception) {
        p2vFinishRefresh()
        if (v2pIsRefresh()) {
            p2vRefreshError(exception.message.orEmpty())
        } else {
            p2vLoadMoreFail()
        }
        resetLoadMoreFailIndex()
    }

    /**
     * 展示数据
     */
    abstract fun p2vShowList(refresh: Boolean, dataList: List<T>)

    private fun dealDataEmpty() {
        p2vFinishRefresh()
        if (v2pIsRefresh()) {
            p2vRefreshEmpty()
        } else {
            p2vLoadMoreEmpty()
        }
        resetLoadMoreFailIndex()
    }

    /**
     * 结束加载中的UI
     */
    abstract fun p2vFinishRefresh()

    /**
     * 数据为空
     */
    open fun p2vRefreshEmpty() {
        requireView.stateController.showEmptyView()
    }

    /**
     * 刷新失败
     */
    open fun p2vRefreshError(message: String) {
        requireView.stateController.showRetryView(message)
    }

    /**
     * 没有更多
     */
    abstract fun p2vLoadMoreEmpty()

    /**
     * 加载更多失败
     */
    abstract fun p2vLoadMoreFail()


    private fun resetLoadMoreFailIndex() {
        if (v2pIsLoadMore() && current > 1) {
            current--
        }
    }
}