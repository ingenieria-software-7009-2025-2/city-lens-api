package com.aperture_science.city_lens_api.user.repository.entity

import com.aperture_science.city_lens_api.util.HashUtil
import jakarta.persistence.*

@Entity
@Table()
data class Usuario (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val email: String = "",
    @Column(name = "first_name")
    val firstName: String,
    @Column(name = "last_name")
    val lastName: String = "",
    @Column(name = "password")
    var password: String="",

)