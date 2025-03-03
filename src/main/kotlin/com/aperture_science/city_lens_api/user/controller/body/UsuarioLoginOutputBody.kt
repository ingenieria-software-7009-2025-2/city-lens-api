package com.aperture_science.city_lens_api.user.controller.body

import com.aperture_science.city_lens_api.user.repository.entity.Usuario

/**
 * Clase que representa los campos de logout de un usuario
 * en el sistema.
 */
data class UsuarioLoginOutputBody (
    val token: String,
    val user: Usuario
)