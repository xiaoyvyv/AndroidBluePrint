package com.xiaoyv.widget.binder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseDifferAdapter
import com.xiaoyv.widget.kts.findBindingCls

/**
 * BaseQuickDiffBindingAdapter
 *
 * @author why
 * @since 11/19/23
 */
abstract class BaseQuickDiffBindingAdapter<T, VB : ViewBinding> constructor(
    config: AsyncDifferConfig<T>,
    items: List<T>
) : BaseDifferAdapter<T, BaseQuickBindingHolder<VB>>(config, items) {

    constructor(diffCallback: DiffUtil.ItemCallback<T>) : this(diffCallback, emptyList())

    constructor(diffCallback: DiffUtil.ItemCallback<T>, items: List<T>) : this(
        AsyncDifferConfig.Builder(diffCallback).build(), items
    )

    constructor(config: AsyncDifferConfig<T>) : this(config, emptyList())

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