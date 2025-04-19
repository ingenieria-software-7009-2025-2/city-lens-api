package com.aperture_science.city_lens_api.report.controller.body

import java.time.LocalDateTime

/**
 * Clase que representa la información necesaria para actualizar un reporte existente.
 */
data class ReportUpdateBody(
    val title: String? = null, // Nuevo título del reporte (opcional)
    val description: String? = null, // Nueva descripción del incidente (opcional)
    val status: String? = null, // Nuevo estado del reporte (opcional)
    val resolutionDate: LocalDateTime? = null // Fecha de resolución (opcional)
)