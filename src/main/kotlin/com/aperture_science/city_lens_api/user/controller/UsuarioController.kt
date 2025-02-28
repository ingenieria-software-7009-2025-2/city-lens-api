package com.aperture_science.city_lens_api.user.controller

import com.aperture_science.city_lens_api.UsuarioRegisterBody
import com.aperture_science.city_lens_api.user.controller.body.UsuarioLoginBody
import com.aperture_science.city_lens_api.user.repository.entity.Usuario
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping
class UsuarioController {

    @PostMapping("/v1/users/login")
    fun Login(@RequestBody userCredentials: UsuarioLoginBody): ResponseEntity<Usuario> {
        val loginUser = Usuario(
            email = userCredentials.email,
            password = userCredentials.password
        )
        return ResponseEntity.ok(loginUser)
    }
    @PostMapping("/v1/users/register")
    fun Rogin(@RequestBody userCredentials: UsuarioRegisterBody): ResponseEntity<Usuario> {
        val loginUser = Usuario(
            name = userCredentials.name,
            email = userCredentials.email,
            password = userCredentials.password
        )
        return ResponseEntity.ok(loginUser)
    }
    @PostMapping("/v1/users/logout")
    fun Logout(): ResponseEntity<String> {
        return ResponseEntity("Sesion cerrada", HttpStatus.OK)
    }
    @GetMapping("/v1/users/me")
    fun GetMyUser(): ResponseEntity<Usuario> {
        return ResponseEntity.ok( Usuario(
            name = "Paquito",
            email = "paquito@example.com",
            password="123456",
            token="0"
        )
        )
    }
}