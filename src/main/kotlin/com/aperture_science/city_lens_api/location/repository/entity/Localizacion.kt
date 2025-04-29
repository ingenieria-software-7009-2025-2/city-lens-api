package com.aperture_science.city_lens_api.location.repository.entity

import jakarta.persistence.*
import java.util.UUID

/**
 * Entidad que representa una ubicación en la base de datos.
 * Mapea la tabla "Location" con campos básicos de ubicación.
 */
@Entity
@Table(name = "Location")
data class Localizacion(
    @Id
    @Column(name = "location_uuid")
    val id: UUID,

    @Column(name = "latitude")
    val latitude: Double,

    @Column(name = "longitude")
    val longitude: Double,

    @Column(name = "direction")
    val direction: String,

    @Column(name = "city")
    val city: String,

    @Column(name = "country")
    val country: String
)