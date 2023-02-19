package com.xiaoyv.widget.listener

import com.google.android.material.tabs.TabLayout

/**
 * SimpleTabListener
 *
 * @author why
 * @since 2023/2/16
 */
inline fun TabLayout.addOnTabListener(
    crossinline onTabSelected: (TabLayout.Tab) -> Unit = {},
    crossinline onTabUnselected: (TabLayout.Tab) -> Unit = {},
    crossinline onTabReselected: (TabLayout.Tab) -> Unit = {}
): TabLayout.OnTabSelectedListener {
    val listener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            onTabSelected.invoke(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {
            onTabUnselected.invoke(tab)
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            onTabReselected.invoke(tab)
        }
    }
    addOnTabSelectedListener(listener)
    return listener
}