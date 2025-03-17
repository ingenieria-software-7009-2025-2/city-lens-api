package com.aperture_science.city_lens_api

/**
 * Clase que representa la información del usuario necesaria
 * para registrarlo en la tabla Users de la BD.
 */
class UsuarioRegisterBody(
    val first_name: String = "",
    val last_name: String = "",
    val email: String = "",
    val password: String = ""

)