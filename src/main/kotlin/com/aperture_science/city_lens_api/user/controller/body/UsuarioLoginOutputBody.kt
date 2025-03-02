package com.aperture_science.city_lens_api.user.controller.body

import com.aperture_science.city_lens_api.user.repository.entity.Usuario

data class UsuarioLoginOutputBody (
    val token: String,
    val user: Usuario
)