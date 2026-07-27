// jvmMain/utils/NetworkMonitor.jvm.kt
package org.example.adminfoodapp.utils

import kotlinx.coroutines.*
import java.net.InetSocketAddress
import java.net.Socket

actual class NetworkMonitor {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var monitorJob: Job? = null

    actual fun register(onAvailable: () -> Unit, onLost: () -> Unit) {
        monitorJob?.cancel()
        var wasConnected: Boolean? = null   // null = pehli baar check ho raha hai

        monitorJob = scope.launch {
            while (isActive) {
                val connected = isInternetAvailable()

                if (wasConnected != connected) {
                    if (connected) onAvailable() else onLost()
                    wasConnected = connected
                }

                delay(3000)
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            Socket().use {
                it.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}