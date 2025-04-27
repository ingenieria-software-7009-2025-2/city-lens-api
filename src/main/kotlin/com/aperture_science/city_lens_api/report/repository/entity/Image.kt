package com.aperture_science.city_lens_api.report.repository.entity

import jakarta.persistence.*
import java.util.UUID
@Entity
@Table(name = "Image")
class Image(
    @Id
    @Column(name = "image_uuid")
    val id: UUID,
    @Column(name = "image_url")
    val imageURL: String
)