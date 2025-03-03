package com.aperture_science.city_lens_api.user.repository.entity

import com.aperture_science.city_lens_api.util.HashUtil
//import com.aperture_science.city_lens_api.user.repository.entity.Usuario
import jakarta.persistence.*

/**
 * Entidad que representa un token de sesión asociado a un usuario.
 * Almacena tokens generados para autenticación y los vincula a un [Usuario].
 */
@Entity
@Table
data class SessionToken(
    /** Token generado (hashed) para la sesión */
    @Id
    var token: String = "",

    /** Usuario asociado al token */
    @ManyToOne
    @JoinColumn(name = "user_id")
    val userID: Usuario
) {
    companion object {
        /**
         * Genera un nuevo token de sesión para un usuario.
         *
         * @param user Usuario al que se asocia el token
         * @return [SessionToken] con token hasheado basado en email + timestamp
         */
        fun createToken(user: Usuario): SessionToken {
            return SessionToken(
                token = HashUtil.hash(user.email + System.currentTimeMillis()),
                userID = user
            )
        }
    }
}

