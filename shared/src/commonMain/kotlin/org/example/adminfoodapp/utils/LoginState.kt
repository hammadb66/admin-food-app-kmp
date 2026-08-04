package org.example.adminfoodapp.utils

sealed class LoginState<out T> {
    object Idle : LoginState<Nothing>()
    object Loading : LoginState<Nothing>()

    data class Success<T>(
        val data: T
    ) : LoginState<T>()

    data class Error(
        val message: String
    ) : LoginState<Nothing>()
}