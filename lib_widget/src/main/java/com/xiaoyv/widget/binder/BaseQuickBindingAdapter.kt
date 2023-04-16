@file:Suppress("UNCHECKED_CAST")

package com.xiaoyv.widget.binder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xiaoyv.widget.kts.findBindingCls

abstract class BaseQuickBindingAdapter<T, VB : ViewBinding>(data: MutableList<T>? = arrayListOf()) :
    BaseQuickAdapter<T, BaseQuickBindingHolder<VB>>(0, data) {

    override fun convert(holder: BaseQuickBindingHolder<VB>, item: T) {
        holder.converted(item)
    }

    protected abstract fun BaseQuickBindingHolder<VB>.converted(item: T)

    override fun createBaseViewHolder(
        parent: ViewGroup,
        layoutResId: Int
    ): BaseQuickBindingHolder<VB> {
        val method = findBindingCls().getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        val vb = method.invoke(null, LayoutInflater.from(context), parent, false) as VB
        return BaseQuickBindingHolder(vb)
    }
}