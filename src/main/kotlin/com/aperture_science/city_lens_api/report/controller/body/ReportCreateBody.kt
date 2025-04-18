package com.aperture_science.city_lens_api.report.controller.body

import java.util.UUID

/**
 * Clase que representa la información necesaria para crear un nuevo reporte.
 */
data class ReportCreateBody(
    val userId: UUID, // ID del usuario que crea el reporte
    val title: String, // Título del reporte
    val description: String, // Descripción del incidente
    val status: String = "open", // Estado inicial del reporte (por defecto "open")
    val locationId: UUID, // ID de la ubicación asociada al reporte
    val imageId: UUID? = null // ID de la imagen asociada (opcional)
)