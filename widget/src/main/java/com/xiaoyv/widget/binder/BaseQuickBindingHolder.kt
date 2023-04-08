package com.xiaoyv.widget.binder

import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class BaseQuickBindingHolder<VB : ViewBinding>(val binding: VB) : BaseViewHolder(binding.root)