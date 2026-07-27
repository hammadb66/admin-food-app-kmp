package org.example.adminfoodapp.utils

sealed class UserState<out T> {
    object Loading : UserState<Nothing>()
    data class Success<T>(val user: T) : UserState<T>()
    data class Error(val message: String) : UserState<Nothing>()
}