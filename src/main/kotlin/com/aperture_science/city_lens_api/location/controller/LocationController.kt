package com.aperture_science.city_lens_api.location.controller

import com.aperture_science.city_lens_api.location.controller.body.*
import com.aperture_science.city_lens_api.location.service.LocationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controlador para gestionar las operaciones relacionadas con ubicaciones.
 */
@RestController
@RequestMapping("/v1/locations")
class LocationController {

    /**
     * Crea una nueva ubicación.
     *
     * @param locationBody Datos de la ubicación a crear.
     * @return La ubicación creada.
     */
    @PostMapping
    fun createLocation(@RequestBody locationBody: LocationCreateBody): ResponseEntity<LocationOutputBody> {
        val location = LocationService.createLocation(locationBody)
        val response = LocationOutputBody(
            id = location.id,
            latitude = location.latitude,
            longitude = location.longitude,
            direction = location.direction,
            city = location.city,
            country = location.country
        )
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    /**
     * Obtiene una ubicación por su ID.
     *
     * @param id ID de la ubicación a buscar.
     * @return La ubicación encontrada o un error 404 si no existe.
     */
    @GetMapping("/{id}")
    fun getLocationById(@PathVariable id: UUID): ResponseEntity<LocationOutputBody> {
        val location = LocationService.getLocationById(id)
        return if (location != null) {
            ResponseEntity.ok(location)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Actualiza una ubicación existente.
     *
     * @param id ID de la ubicación a actualizar.
     * @param locationBody Datos actualizados de la ubicación.
     * @return La ubicación actualizada o un error 404 si no existe.
     */
    @PutMapping("/{id}")
    fun updateLocation(
        @PathVariable id: UUID,
        @RequestBody locationBody: LocationUpdateBody
    ): ResponseEntity<LocationOutputBody> {
        val updatedLocation = LocationService.updateLocation(id, locationBody)
        return if (updatedLocation != null) {
            val response = LocationOutputBody(
                id = updatedLocation.id,
                latitude = updatedLocation.latitude,
                longitude = updatedLocation.longitude,
                direction = updatedLocation.direction,
                city = updatedLocation.city,
                country = updatedLocation.country
            )
            ResponseEntity.ok(response)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /**
     * Elimina una ubicación por su ID.
     *
     * @param id ID de la ubicación a eliminar.
     * @return Respuesta de éxito o error 404 si no existe.
     */
    @DeleteMapping("/{id}")
    fun deleteLocation(@PathVariable id: UUID): ResponseEntity<Void> {
        val deleted = LocationService.deleteLocation(id)
        return if (deleted) {
            ResponseEntity(HttpStatus.NO_CONTENT)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}