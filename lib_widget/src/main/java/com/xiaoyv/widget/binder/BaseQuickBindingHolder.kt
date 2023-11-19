package com.xiaoyv.widget.binder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BaseQuickBindingHolder<VB : ViewBinding>(val binding: VB) :
    RecyclerView.ViewHolder(binding.root)