package com.aperture_science.city_lens_api.location.controller

import com.aperture_science.city_lens_api.location.controller.body.*
import com.aperture_science.city_lens_api.location.service.LocationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.UUID

/**
 * Controlador para gestionar las operaciones relacionadas con ubicaciones.
 */
@RestController
@RequestMapping
@Tag(name = "Location", description = "Operaciones para crear, consultar, actualizar y eliminar ubicaciones dentro del sistema.")

class LocationController {

    /**
     * Crea una nueva ubicación en la base de datos.
     *
     * @param locationBody Datos de la ubicación a crear.
     * @return La ubicación creada.
     */
    @PostMapping("/v1/location/create")
    @Operation(
        summary = "Crear una nueva ubicación",
        description = "Permite registrar una nueva ubicación en el sistema.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos necesarios para crear una nueva ubicación",
            required = true,
            content = [io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = io.swagger.v3.oas.annotations.media.Schema(implementation = LocationCreateBody::class)
            )]
        ),
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Ubicación creada exitosamente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = io.swagger.v3.oas.annotations.media.Schema(implementation = LocationOutputBody::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Datos inválidos para crear la ubicación",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain"
                )]
            )
        ]
    )
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
    @GetMapping("/v1/location/{id}")
    @Operation(
        summary = "Obtener ubicación por ID",
        description = "Recupera una ubicación específica usando su identificador único.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Ubicación encontrada exitosamente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = io.swagger.v3.oas.annotations.media.Schema(implementation = LocationOutputBody::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Ubicación no encontrada",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain"
                )]
            )
        ]
    )
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
    @PutMapping("/v1/location/{id}")
    @Operation(
        summary = "Actualizar ubicación",
        description = "Actualiza los datos de una ubicación existente usando su ID.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos de la ubicación a actualizar",
            required = true,
            content = [io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = io.swagger.v3.oas.annotations.media.Schema(implementation = LocationUpdateBody::class)
            )]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Ubicación actualizada exitosamente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = io.swagger.v3.oas.annotations.media.Schema(implementation = LocationOutputBody::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Ubicación no encontrada",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain"
                )]
            )
        ]
    )
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
     * @return Respuesta de éxito (204) o error 404 si no existe la ubicación.
     */
    @DeleteMapping("/v1/location/{id}")
    @Operation(
        summary = "Eliminar una ubicación",
        description = "Elimina una ubicación existente utilizando su identificador único.",
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "Ubicación eliminada exitosamente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain"
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Ubicación no encontrada",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain"
                )]
            )
        ]
    )
    fun deleteLocation(@PathVariable id: UUID): ResponseEntity<Void> {
        val deleted = LocationService.deleteLocation(id)
        return if (deleted) {
            ResponseEntity(HttpStatus.NO_CONTENT)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}