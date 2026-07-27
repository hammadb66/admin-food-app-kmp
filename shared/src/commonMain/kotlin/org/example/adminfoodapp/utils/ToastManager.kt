package org.example.adminfoodapp.utils

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ToastManager(private val scope: CoroutineScope) {
    val snackbarHostState = SnackbarHostState()

    fun show(message: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
}