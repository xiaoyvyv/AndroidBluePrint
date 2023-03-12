package com.xiaoyv.widget.kts

import android.app.Dialog
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog

val <T : Fragment> T.fragment get() = this

fun DialogFragment.createFixFocusDialog(): Dialog =
    object : Dialog(requireActivity(), theme) {
        override fun dismiss() {
            dismissSoftInput()
            super.dismiss()
        }
    }

fun DialogFragment.createFixFocusBottomSheetDialog(): BottomSheetDialog =
    object : BottomSheetDialog(requireActivity(), theme) {
        override fun dismiss() {
            dismissSoftInput()
            super.dismiss()
        }
    }

fun DialogFragment.dismissSoftInput() {
    dialog?.currentFocus?.apply {
        val systemService: InputMethodManager? =
            ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)

        useNotNull(systemService) {
            hideSoftInputFromWindow(windowToken, 0)
        }
    }
}

/**
 * 判断 DialogFragment 是否可以显示
 */
fun DialogFragment.canShowInFragment(fragment: Fragment): Boolean {
    if (isAdded || isRemoving || isVisible || fragment.childFragmentManager.isDestroyed) {
        return false
    }
    return true
}