package com.aperture_science.city_lens_api.modules.config.framework.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controlador de estado de servicio para monitoreo básico.
 * Expone endpoints simples para verificar que la API está operativa.
 */
@RestController
@RequestMapping("/v1/health")
class HealthController {

    /**
     * Verificación básica de disponibilidad
     * @return Mensaje de bienvenida en español con status 200
     */
    @GetMapping
    fun retrieveHealth(): ResponseEntity<String> {
        return ResponseEntity.ok("Hola!. Bienvenido a City Lens")
    }

    /**
     * Endpoint de prueba con respuesta numérica fija
     * @return "444" con status 200 (solo para demostración)
     */
    @PostMapping
    fun retrieveInt(): ResponseEntity<Int> {
        return ResponseEntity.ok(444)
    }
}