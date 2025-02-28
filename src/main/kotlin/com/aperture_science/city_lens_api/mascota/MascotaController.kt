package com.aperture_science.city_lens_api.mascota

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/mascotas")
class MascotaController {

    @GetMapping
    fun retrieveMascota(): ResponseEntity<Mascota> {
        val miMascota = Mascota(tipo = "Gato", name = "Octo", peso = "8kg")
        return ResponseEntity.ok(miMascota)
    }

    @PostMapping
    fun createMascota(@RequestBody mascotaBody: MascotaBody): ResponseEntity<Mascota> {
        val miMascota = Mascota(
            tipo = mascotaBody.tipo,
            name = mascotaBody.name,
            peso = mascotaBody.peso)
        return ResponseEntity.ok(miMascota)
    }
}