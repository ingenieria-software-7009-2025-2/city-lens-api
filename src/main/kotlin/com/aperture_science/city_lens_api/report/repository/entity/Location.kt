package com.aperture_science.city_lens_api.report.repository.entity

import jakarta.persistence.*
/**
 * Entidad que representa una ubicación de un reporte en la base de datos.
 * Mapea la tabla "Location" con las columnas definidas en el esquema.
 */
@Entity
@Table(name = "Location")
data class Location (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "location_id")
    val locationId: Int,
    val latitude: Double,
    val longitude: Double,
    val direction: String,
    val zipcode: String,
    val municipality: String,


)