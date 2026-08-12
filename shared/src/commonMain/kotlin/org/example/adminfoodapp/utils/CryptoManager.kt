package org.example.adminfoodapp.utils

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

object CryptoManager {
    // Ye chabi (key) hai. Isko hamesha secret rakhna! 16 characters ki honi chahiye.
    private const val SECRET_KEY = "MySuperSecretKey"

    fun encrypt(data: String): String {
        return try {
            val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            val encryptedBytes = cipher.doFinal(data.toByteArray())
            Base64.getEncoder().encodeToString(encryptedBytes)
        } catch (e: Exception) {
            "" // Agar koi masla aye toh khali string return kar do
        }
    }

    fun decrypt(encryptedData: String): String {
        return try {
            val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            val decodedBytes = Base64.getDecoder().decode(encryptedData)
            val decryptedBytes = cipher.doFinal(decodedBytes)
            String(decryptedBytes)
        } catch (e: Exception) {
            ""
        }
    }
}