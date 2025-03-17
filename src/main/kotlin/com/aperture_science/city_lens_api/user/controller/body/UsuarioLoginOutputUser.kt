package com.aperture_science.city_lens_api.user.controller.body

import java.util.UUID

data class UsuarioLoginOutputUser(
    val id: UUID,
    val email: String,
    val first_name: String,
    val last_name: String,
    val role: String
)