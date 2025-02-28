package com.aperture_science.city_lens_api.user.repository.entity

import jakarta.persistance.*

@Entity
@Table()
data class Usuario (
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val token: String = "",
)