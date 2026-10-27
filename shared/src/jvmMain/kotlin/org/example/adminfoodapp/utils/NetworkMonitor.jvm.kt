package org.example.adminfoodapp.utils

import kotlinx.coroutines.*
import java.net.InetAddress

actual class NetworkMonitor {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var monitorJob: Job? = null

    actual fun register(onAvailable: () -> Unit, onLost: () -> Unit) {
        monitorJob?.cancel()
        var wasConnected: Boolean? = null

        monitorJob = scope.launch {
            while (isActive) {
                val connected = isInternetAvailable()
                if (wasConnected != connected) {
                    if (connected) onAvailable() else onLost()
                    wasConnected = connected
                }
                delay(30_000)   // 👈 3 sec → 30 sec
            }
        }
    }

    actual fun unregister() {
        monitorJob?.cancel()
        monitorJob = null
    }

    private fun isInternetAvailable(): Boolean = try {
        InetAddress.getByName("1.1.1.1").isReachable(2000)
    } catch (_: Exception) {
        false
    }
}