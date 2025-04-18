package com.aperture_science.city_lens_api.location.controller.body

/**
 * Clase que representa la información necesaria para crear una nueva ubicación.
 */
data class LocationCreateBody(
    val latitude: Double,
    val longitude: Double,
    val direction: String = "",
    val city: String,
    val country: String
)