package com.xiaoyv.blueprint.base

/**
 * IBaseContract
 *
 * @author why
 * @since 2020/11/28
 */
interface IBaseContract {
    interface View : IBaseView
    interface Presenter : IBasePresenter
    interface Model : IBaseModel
}