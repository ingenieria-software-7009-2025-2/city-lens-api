package com.aperture_science.city_lens_api.user.repository.entity

import com.aperture_science.city_lens_api.util.HashUtil
import com.aperture_science.city_lens_api.user.repository.entity.Usuario
import jakarta.persistence.*

@Entity
@Table()
data class SessionToken(
    @Id
    var token: String = "",
    @ManyToOne
    @JoinColumn(name = "user_id")
    val userID: Long
) {
    companion object {
        fun createToken(user: Usuario): SessionToken {
            return SessionToken(
                token= HashUtil.hash(user.email + System.currentTimeMillis()),
                userID = user.id
            )
        }
    }
}

