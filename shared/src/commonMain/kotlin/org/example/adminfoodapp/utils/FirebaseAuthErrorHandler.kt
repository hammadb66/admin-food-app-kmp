package org.example.adminfoodapp.utils

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

object FirebaseAuthErrorHandler {

    /**
     * Translates Firebase Auth Exceptions into user-friendly error messages.
     */
    fun getLocalizedMessage(e: Exception): String {
        return when (e) {
            is FirebaseAuthInvalidUserException -> {
                // Catches: Account doesn't exist, or account has been disabled
                "No account found with this email address."
            }
            is FirebaseAuthInvalidCredentialsException -> {
                // Catches: Wrong password, or poorly formatted email
                "Incorrect password or invalid email format."
            }
            is FirebaseAuthUserCollisionException -> {
                // Catches: Email already in use (e.g., trying to sign up with an email
                // that is already registered via Google/Facebook login)
                "This email is already registered. Try signing in with Google or Facebook."
            }
            else -> {
                // Fallback for network issues, timeout, or unknown server errors
                e.localizedMessage ?: "A network error occurred. Please try again."
            }
        }
    }
}
