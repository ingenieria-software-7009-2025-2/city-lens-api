package com.aperture_science.city_lens_api.util

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

/**
 * Clase que usada para generar los
 */
class HashUtil {
    companion object {
        /**
         * Genera un hash dado un string.
         * @return string hasheado en SHA-256
         */
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

        /**
         * Call to Action para generateHash()
         * @return String hasheado en SHA-256.
         */
        fun hash(value: String): String {
            return generateHash(value)
        }

    }
}