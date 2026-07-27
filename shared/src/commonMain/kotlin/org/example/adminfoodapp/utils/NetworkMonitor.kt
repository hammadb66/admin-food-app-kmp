package org.example.adminfoodapp.utils

expect class NetworkMonitor {
    fun register(onAvailable: () -> Unit, onLost: () -> Unit)
}