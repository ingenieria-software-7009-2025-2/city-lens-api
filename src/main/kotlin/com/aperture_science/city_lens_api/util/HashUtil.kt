package com.aperture_science.city_lens_api.util
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import kotlin.experimental.and

class HashUtil {
    //Hash function to hash a string
    companion object {
        private fun generateHash(value: String): String {
            try {
                val messageDigest = MessageDigest.getInstance("SHA-256")
                val bytes = messageDigest.digest(value.toByteArray())
                val sb = StringBuilder()
                for (i in bytes.indices) {
                    sb.append(((bytes[i] and 0xff.toByte()) + 0x100).toString(16).substring(1))
                }
                return sb.toString()
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }
        }
        fun hash(value: String): String {
            return generateHash(value)
        }

    }
}