package com.aperture_science.city_lens_api.location.service

import com.aperture_science.city_lens_api.location.controller.body.*
import com.aperture_science.city_lens_api.location.repository.LocationRepository
import com.aperture_science.city_lens_api.location.repository.entity.Localizacion
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID

class LocationService {
    companion object {

        fun createLocation(locationBody: LocationCreateBody): Localizacion {
            val location = Localizacion(
                id = UUID.randomUUID(),
                latitude = locationBody.latitude,
                longitude = locationBody.longitude,
                direction = locationBody.direction,
                city = locationBody.city,
                country = locationBody.country
            )
            LocationRepository.persistLocation(location)
            return location
        }

        fun getLocationById(id: UUID): LocationOutputBody? {
            val location = LocationRepository.getLocationById(id)
            return location?.let {
                LocationOutputBody(
                    id = it.id,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    direction = it.direction,
                    city = it.city,
                    country = it.country
                )
            }
        }

        fun updateLocation(id: UUID, locationBody: LocationUpdateBody): Localizacion? {
            val existingLocation = LocationRepository.getLocationById(id)
            return if (existingLocation != null) {
                val updatedLocation = existingLocation.copy(
                    latitude = locationBody.latitude ?: existingLocation.latitude,
                    longitude = locationBody.longitude ?: existingLocation.longitude,
                    direction = locationBody.direction ?: existingLocation.direction,
                    city = locationBody.city ?: existingLocation.city,
                    country = locationBody.country ?: existingLocation.country
                )
                LocationRepository.updateLocation(existingLocation, updatedLocation)
                updatedLocation
            } else {
                null
            }
        }


        fun deleteLocation(id: UUID): Boolean {
            val location = LocationRepository.getLocationById(id)
            return if (location != null) {
                LocationRepository.deleteLocation(location)
                true
            } else {
                false
            }
        }
    }
}