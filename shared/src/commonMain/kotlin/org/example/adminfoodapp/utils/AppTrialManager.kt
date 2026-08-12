package org.example.adminfoodapp.utils

import com.russhwolf.settings.Settings
import java.io.File
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

object AppTrialManager {

    private const val KEY_START_TIME = "ENC_START_TIME"
    private const val KEY_LAST_SEEN = "ENC_LAST_SEEN"

    // 7 Din milliseconds mein
    private const val SEVEN_DAYS_MILLIS = 7L * 24 * 60 * 60 * 1000

    // ------------------------------------------------------------
    // YE ASLI "MOHAR" (signature) HAI - tumhare purane "reverse+XYZ"
    // ki jagah. Ye secret key koi bhi user Settings file khol ke
    // dekh ke reverse-engineer nahi kar sakta, kyunki HMAC one-way
    // hota hai (jaise ungli ka nishaan - tum ungli se nishaan bana
    // sakte ho, lekin sirf nishaan dekh ke ungli nahi bana sakte).
    // Production mein isay scatter/obfuscate kar dena, seedha string
    // na chhodna.
    // ------------------------------------------------------------
    private const val SECRET_KEY = "R3st0-App!Tr1al#S3cr3t-2026-XyZ"

    private fun sign(startTime: Long, lastSeen: Long): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(SECRET_KEY.toByteArray(), "HmacSHA256"))
        val raw = "$startTime:$lastSeen".toByteArray()
        return Base64.getEncoder().encodeToString(mac.doFinal(raw))
    }

    // Data ko ek string mein pack karna: "start|lastSeen|mohar"
    private fun pack(startTime: Long, lastSeen: Long): String =
        "$startTime|$lastSeen|${sign(startTime, lastSeen)}"

    // String ko khol ke mohar verify karna. Agar mohar galat hai
    // (kisi ne values edit ki hain), null return hota hai -> tampering.
    private fun unpackAndVerify(data: String?): Pair<Long, Long>? {
        if (data.isNullOrBlank()) return null
        val parts = data.split("|")
        if (parts.size != 3) return null

        val startTime = parts[0].toLongOrNull() ?: return null
        val lastSeen = parts[1].toLongOrNull() ?: return null
        val storedSignature = parts[2]
        val expectedSignature = sign(startTime, lastSeen)

        // constant-time compare - timing attack se bachne ke liye
        if (!MessageDigest.isEqual(storedSignature.toByteArray(), expectedSignature.toByteArray())) {
            return null // Koi cheda-chaadi hui hai!
        }
        return startTime to lastSeen
    }

    // ------------------------------------------------------------
    // DOOSRI DIARY (hidden file). appDataDir tum apne Splash/App
    // start hote waqt bana ke pass kar dena, masalan:
    // File(System.getProperty("user.home"), ".adminfoodapp")
    // ------------------------------------------------------------
    private fun hiddenFile(appDataDir: File): File {
        if (!appDataDir.exists()) appDataDir.mkdirs()
        return File(appDataDir, ".trial_data")
    }

    private fun writeEverywhere(settings: Settings, appDataDir: File, startTime: Long, lastSeen: Long) {
        val packed = pack(startTime, lastSeen)
        settings.putString(KEY_START_TIME, packed) // hum dono values ek hi key mein rakh rahe hain
        settings.putString(KEY_LAST_SEEN, packed)   // (backward-compatible naming ke liye dono key same likh di)
        hiddenFile(appDataDir).writeText(packed)
    }

    /**
     * @param appDataDir - ek folder jahan hum doosri "diary" (hidden file) rakhenge.
     *                     SplashViewModel banate waqt ye pass kar dena.
     */
    fun isTrialValid(settings: Settings, appDataDir: File): Boolean {
        val currentTime = System.currentTimeMillis()

        val fromSettings = unpackAndVerify(settings.getStringOrNull(KEY_START_TIME))
        val fileData = hiddenFile(appDataDir).takeIf { it.exists() }?.readText()
        val fromFile = unpackAndVerify(fileData)

        val candidates = listOfNotNull(fromSettings, fromFile)

        // SCENARIO 1: Bilkul naya user - kahin bhi record nahi
        if (candidates.isEmpty()) {
            writeEverywhere(settings, appDataDir, currentTime, currentTime)
            return true
        }

        // SCENARIO 2: Dono jagah record mila lekin startTime match nahi karta
        // -> kisi ne ek jagah edit ki (tampering)
        if (fromSettings != null && fromFile != null && fromSettings.first != fromFile.first) {
            println("❌ CHOR PAKRA GAYA! Do alag jagah alag data mila - tampering.")
            return false
        }

        // Ek jagah se mila (doosri missing thi, ho sakta hai delete hui ho) - us se restore
        val (startTime, lastSeenTime) = candidates.first()

        // 🔥 CHORI PAKARNE WALI LOGIC (Clock Rollback) 🔥
        if (currentTime < lastSeenTime) {
            println("❌ CHOR PAKRA GAYA! User ne computer ki date piche ki hai.")
            return false
        }

        // ⏱️ 7 DAYS EXPIRY CHECK ⏱️
        val timePassed = currentTime - startTime
        if (timePassed > SEVEN_DAYS_MILLIS) {
            println("❌ 7 Din khatam ho gaye!")
            return false
        }

        // SCENARIO 3: Sab theek hai - lastSeen update karo dono jagah
        writeEverywhere(settings, appDataDir, startTime, currentTime)
        return true
    }
}