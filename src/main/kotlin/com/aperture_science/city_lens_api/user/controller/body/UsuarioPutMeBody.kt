package com.aperture_science.city_lens_api.user.controller.body

/**
 * Clase que representa la información del usuario necesaria
 * para realizar un PUT usando v1/users/me
 */
class UsuarioPutMeBody(
    val first_name: String? = null,
    val last_name: String? = null,
    val email: String? = null,
    val password: String? = null
)