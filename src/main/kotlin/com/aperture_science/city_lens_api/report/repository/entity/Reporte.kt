package com.aperture_science.city_lens_api.report.repository.entity

import com.aperture_science.city_lens_api.location.repository.entity.Localizacion
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
    val id: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid")
    val userUUID: UUID,

    @Column(name = "title")
    val title: String,

    @Column(name = "description")
    val description: String,

    @Column(name = "status")
    val status: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    val locationID: Int,

    @Column(name = "creationDate")
    val creationDate: LocalDateTime,

    @Column(name = "resolutionDate")
    val resolutionDate: LocalDateTime?,

    @Column(name = "image_uuid")
    val imageId: UUID?=null
)