@file:Suppress("DEPRECATION")

package com.xiaoyv.blueprint.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.blankj.utilcode.util.Utils


/**
 * VpnUtils
 *
 * @author why
 * @since 2022/1/12
 */
object VpnUtils {

    /**
     * 检测 VPN 是否处于连接中
     */
    fun isVpnEnable(): Boolean {
        var vpnInUse = false
        val manager = Utils.getApp()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = manager.activeNetwork
            val caps = manager.getNetworkCapabilities(activeNetwork)
            return caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: false
        }
        val networks = manager.allNetworks
        for (i in networks.indices) {
            val caps = manager.getNetworkCapabilities(networks[i]) ?: continue
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                vpnInUse = true
                break
            }
        }

        return vpnInUse
    }
}