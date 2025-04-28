package com.aperture_science.city_lens_api.report.repository.entity

import jakarta.persistence.*
import java.util.UUID

/**
 * Entidad que representa una imagen asociada a un reporte en el sistema.
 *
 * Esta clase mapea la tabla "Image" en la base de datos y contiene la información básica
 * necesaria para almacenar y recuperar imágenes relacionadas con los reportes.
 * 
 * Cada imagen tiene un identificador único (UUID) y una URL donde está almacenada físicamente.
 */
@Entity
@Table(name = "Image")
class Image(
    @Id
    @Column(name = "image_uuid")
    val id: UUID,

    /**
     * URL de acceso a la imagen almacenada.
     * 
     * Esta cadena contiene la ubicación física (ruta o URL) donde se encuentra almacenado
     * el archivo de imagen asociado a un reporte.
     */
    @Column(name = "image_url")
    val imageURL: String
)