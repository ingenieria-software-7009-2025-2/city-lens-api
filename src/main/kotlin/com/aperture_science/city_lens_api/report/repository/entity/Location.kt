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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    val locationId: Int,         // ID único autogenerado
    val latitude: Double,       // Coordenada de latitud
    val longitude: Double,      // Coordenada de longitud
    val direction: String,      // Dirección postal descriptiva
    val zipcode: String,        // Código postal de la zona
    val municipality: String, ) // Municipio o división administrativa