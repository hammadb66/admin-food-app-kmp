package org.example.adminfoodapp.utils

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission

actual class NetworkMonitor(private val context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var registeredCallback: ConnectivityManager.NetworkCallback? = null

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    actual fun register(onAvailable: () -> Unit, onLost: () -> Unit) {
        // Purana callback hatao
        registeredCallback?.let {
            runCatching { connectivityManager.unregisterNetworkCallback(it) }
        }
        registeredCallback = null

        // Naya callback banao
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) = onAvailable()
            override fun onLost(network: Network) = onLost()
        }
        registeredCallback = callback
        connectivityManager.registerDefaultNetworkCallback(callback)

        // 👇 IMPORTANT: Agar internet already on hai, abhi fire karo
        val current = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        val isOnlineNow = current?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        if (isOnlineNow) {
            onAvailable()
        }
    }


    // 👇 YE METHOD MISSING THA - ADD KARO
    actual fun unregister() {
        registeredCallback?.let {
            runCatching { connectivityManager.unregisterNetworkCallback(it) }
        }
        registeredCallback = null
    }
}