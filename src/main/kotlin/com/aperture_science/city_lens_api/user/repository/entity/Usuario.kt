package com.aperture_science.city_lens_api.user.repository.entity

import com.aperture_science.city_lens_api.util.HashUtil
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table( name="Users")
data class Usuario (
    @Id
    @Column(name = "user_uuid")
    val id: UUID ,
    val email: String = "",
    @Column(name = "first_name")
    val firstName: String,
    @Column(name = "last_name")
    val lastName: String = "",
    @Column(name = "password_hash")
    var password: String="",
    var role: String = "user"

)