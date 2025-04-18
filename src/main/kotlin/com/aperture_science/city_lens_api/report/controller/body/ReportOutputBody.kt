package com.aperture_science.city_lens_api.report.controller.body

import java.time.LocalDateTime
import java.util.UUID

/**
 * Clase que representa los datos de un reporte que se devuelven al cliente.
 */
data class ReportOutputBody(
    val id: UUID, // ID del reporte
    val title: String, // Título del reporte
    val description: String, // Descripción del incidente
    val status: String, // Estado del reporte
    val locationId: UUID, // ID de la ubicación asociada
    val creationDate: LocalDateTime, // Fecha de creación del reporte
    val resolutionDate: LocalDateTime? = null, // Fecha de resolución (opcional)
    val imageId: UUID? = null // ID de la imagen asociada (opcional)
)