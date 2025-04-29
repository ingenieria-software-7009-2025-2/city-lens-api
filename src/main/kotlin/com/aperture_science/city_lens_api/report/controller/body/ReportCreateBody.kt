package com.aperture_science.city_lens_api.report.controller.body

import java.util.UUID

/**
 * Clase que representa la información necesaria para crear un nuevo reporte.
 */
data class ReportCreateBody(
    val title: String, // Título del reporte
    val description: String, // Descripción del incidente
    val latitude: Double, // Latitud de la ubicación del incidente
    val longitude: Double, // Longitud de la ubicación del incidente
    val direction: String, // Dirección de la ubicación del incidente
    val zipcode: String, // Código postal de la ubicación del incidente
    val municipality: String, // Municipio de la ubicación del incidente
    val imageURL: String? = null // ID de la imagen asociada (opcional)
)