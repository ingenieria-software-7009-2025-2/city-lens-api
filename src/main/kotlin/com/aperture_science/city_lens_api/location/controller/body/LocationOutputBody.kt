package com.aperture_science.city_lens_api.location.controller.body

import java.util.UUID

/**
 * Clase que representa los datos de una ubicación que se devuelven al cliente.
 */
data class LocationOutputBody(
    val id: UUID,
    val latitude: Double,
    val longitude: Double,
    val direction: String,
    val city: String,
    val country: String
)