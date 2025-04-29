package com.aperture_science.city_lens_api.report.repository.entity

import com.aperture_science.city_lens_api.user.repository.entity.Usuario
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

/**
 * Entidad que representa un reporte en la base de datos.
 * Mapea la tabla "Report" con las columnas definidas en el esquema.
 */
@Entity
@Table(name = "Report")
data class Reporte(
    @Id
    @Column(name = "report_uuid")
    val id: UUID,                       // Identificador único del reporte

    @Column(name = "user_uuid")
    val userUUID: UUID,                 // ID del usuario que creó el reporte

    @Column(name = "title")
    val title: String,                  // Título descriptivo del reporte   

    @Column(name = "description")
    val description: String,            // Detalles completos del incidente

    @Column(name = "status")
    val status: String,                 // Estado actual (abierto/cerrado/en progreso)

    @Column(name = "location_id")
    val locationID: Int,                // Referencia a la ubicación física

    @Column(name = "creationDate")
    val creationDate: LocalDateTime,    // Fecha de creación del reporte

    @Column(name = "resolutionDate")
    val resolutionDate: LocalDateTime?,     // Fecha de cierre (null si no resuelto)

    @Column(name = "image_uuid")
    val imageId: UUID?=null             // Referencia opcional a imagen adjunta
)