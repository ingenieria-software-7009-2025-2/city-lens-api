package com.aperture_science.city_lens_api.location.controller.body

/**
 * Clase que representa la información necesaria para actualizar una ubicación existente.
 */
data class LocationUpdateBody(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val direction: String? = null,
    val city: String? = null,
    val country: String? = null
)