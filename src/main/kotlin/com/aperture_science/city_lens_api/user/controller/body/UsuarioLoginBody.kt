package com.aperture_science.city_lens_api.user.controller.body

/**
 * Clase que representa los campos de login de un usuario
 * en la BD.
 */
data class UsuarioLoginBody (
    val email: String = "",
    val password: String = ""
)
