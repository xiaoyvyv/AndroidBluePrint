package com.xiaoyv.widget.binder

import android.view.View
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.binder.QuickViewBindingItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

/**
 * BaseItemBinder
 *
 * @author why
 * @since 2020/12/01
 */
abstract class BaseItemBindingBinder<T, VB : ViewBinding> : QuickViewBindingItemBinder<T, VB>() {
    /**
     * 点击事件
     */
    var onItemChildClickListener: OnItemChildClickListener<T>? = null

    override fun convert(holder: BinderVBHolder<VB>, data: T) {
        convert(holder, holder.viewBinding, data)
        holder.addClickListener(holder.viewBinding.root, data)
    }

    /**
     * 添加 binding 形式参数
     */
    abstract fun convert(holder: BinderVBHolder<VB>, binding: VB, data: T)

    /**
     * Kotlin 简化版本，添加点击事件
     */
    fun <VB : ViewBinding> BinderVBHolder<VB>.addClickListener(
        view: View,
        data: T
    ) {
        view.setOnFastLimitClickListener {
            onItemChildClickListener?.onItemChildClick(view, data, bindingAdapterPosition, false)
        }
    }

    interface OnItemChildClickListener<BEAN> {
        /**
         * 子条目点击事件
         *
         * @param view        点击的 view
         * @param dataBean    数据
         * @param position    位置
         * @param isLongClick 是否长按
         */
        fun onItemChildClick(view: View, dataBean: BEAN, position: Int, isLongClick: Boolean)
    }

}

/**
 * Kotlin 扩展版本
 */
inline fun <reified T, reified VB : ViewBinding> BaseItemBindingBinder<T, VB>.setOnItemClickListener(
    crossinline onItemChildClick: (view: View, dataBean: T, position: Int, isLongClick: Boolean) -> Unit
): BaseItemBindingBinder.OnItemChildClickListener<T> {
    val clickListener = object : BaseItemBindingBinder.OnItemChildClickListener<T> {
        override fun onItemChildClick(
            view: View,
            dataBean: T,
            position: Int,
            isLongClick: Boolean
        ) {
            onItemChildClick.invoke(view, dataBean, position, isLongClick)
        }
    }
    onItemChildClickListener = clickListener
    return clickListener
}

