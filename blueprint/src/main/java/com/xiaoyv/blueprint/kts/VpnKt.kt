@file:Suppress("DEPRECATION")

package com.xiaoyv.blueprint.kts

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.Utils
import com.xiaoyv.widget.kts.useNotNull


/**
 * 检测 VPN 是否处于连接中
 */
val isVpnEnable: Boolean
    get() {
        val application = Utils.getApp()

        useNotNull(ContextCompat.getSystemService(application, ConnectivityManager::class.java)) {
            val caps = getNetworkCapabilities(activeNetwork)
            return caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: false
        }
        return false
    }
