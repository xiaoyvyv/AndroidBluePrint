@file:Suppress("unused")

package com.xiaoyv.blueprint.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xiaoyv.widget.utils.onStartTransparentDialog

/**
 * BaseSheetFragment
 *
 * @author why
 * @since 2022/2/19
 */
abstract class BaseSheetFragment<BINDING : ViewBinding> : BottomSheetDialogFragment() {
    lateinit var binding: BINDING

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = onBinding(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onBindingCreated(binding, savedInstanceState)
    }

    abstract fun onBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): BINDING

    abstract fun onBindingCreated(binding: BINDING, savedInstanceState: Bundle?)

    override fun onStart() {
        super.onStart()
        onStartTransparentDialog(
            skipCollapsed = true
        )
    }

    fun show(fragment: Fragment) {
        if (!isAdded && !isVisible && !isRemoving) {
            show(fragment.childFragmentManager, javaClass.simpleName)
        }
    }

    fun show(fragmentActivity: FragmentActivity) {
        if (!isAdded && !isVisible && !isRemoving) {
            show(fragmentActivity.supportFragmentManager, javaClass.simpleName)
        }
    }
}