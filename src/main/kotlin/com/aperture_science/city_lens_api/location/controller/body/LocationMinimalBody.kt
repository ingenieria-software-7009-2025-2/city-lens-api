package com.aperture_science.city_lens_api.location.controller.body

import java.util.UUID

/**
 * Clase que representa una versión compacta de los datos de una ubicación.
 */
data class LocationMinimalBody(
    val id: UUID,
    val city: String,
    val country: String
)