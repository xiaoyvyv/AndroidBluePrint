@file:Suppress("UNCHECKED_CAST")

package com.xiaoyv.widget.binder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xiaoyv.widget.kts.findBindingCls

abstract class BaseQuickBindingAdapter<T, VB : ViewBinding> @JvmOverloads constructor(
    override var items: List<T> = emptyList()
) : BaseQuickAdapter<T, BaseQuickBindingHolder<VB>>(items) {

    override fun onBindViewHolder(holder: BaseQuickBindingHolder<VB>, position: Int, item: T?) {
        if (item != null) {
            holder.converted(item)
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
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

    protected abstract fun BaseQuickBindingHolder<VB>.converted(item: T)
}