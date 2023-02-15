package com.xiaoyv.widget.binder

import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class BaseQuickBindingHolder<VB : ViewBinding>(internal val binding: VB) :
    BaseViewHolder(binding.root)