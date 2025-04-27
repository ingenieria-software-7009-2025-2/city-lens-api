package com.aperture_science.city_lens_api.location.repository

import com.aperture_science.city_lens_api.location.repository.entity.Localizacion
import com.aperture_science.city_lens_api.util.EntityManagerFactoryInstance
import jakarta.persistence.EntityManager
import java.util.UUID

/**
 * Repositorio para gestionar las operaciones de base de datos relacionadas con las ubicaciones.
 *
 * Esta clase proporciona métodos para interactuar con la base de datos, como guardar ubicaciones,
 * buscar ubicaciones por ID y actualizar información de ubicaciones.
 */
class LocationRepository {
    companion object {

        /**
         * Persiste una ubicación en la base de datos.
         *
         * @param location La ubicación a persistir.
         */
        fun persistLocation(location: Localizacion) {
            val em = getEntityManager()
            em.transaction.begin()
            em.persist(location) // Guarda la ubicación en la base de datos
            em.transaction.commit() // Confirma la transacción
            em.close() // Cierra el EntityManager
        }

        /**
         * Busca una ubicación por su ID.
         *
         * @param id El ID de la ubicación a buscar.
         * @return La ubicación si se encuentra, o `null` si no existe.
         */
        fun getLocationById(id: UUID): Localizacion? {
            val em = getEntityManager()
            val location = em.createQuery("SELECT l FROM Localizacion l WHERE l.id = :id", Localizacion::class.java)
                .setParameter("id", id)
                .resultList
            em.close() // Cierra el EntityManager
            return if (location.isNotEmpty()) location[0] else null
        }


        /**
         * Actualiza la información de una ubicación en la base de datos.
         *
         * @param location La ubicación con la información actual.
         * @param updatedLocation Los cambios a aplicar a la ubicación.
         * @return La ubicación actualizada.
         */
        fun updateLocation(location: Localizacion, updatedLocation: Localizacion): Localizacion {
            val em = getEntityManager()

            // Crea una copia de la ubicación con los cambios aplicados
            val updated = location.copy(
                latitude = updatedLocation.latitude,
                longitude = updatedLocation.longitude,
                direction = updatedLocation.direction,
                city = updatedLocation.city,
                country = updatedLocation.country
            )

            em.transaction.begin()
            em.merge(updated) // Actualiza la ubicación en la base de datos
            em.transaction.commit() // Confirma la transacción
            em.close() // Cierra el EntityManager

            return updated
        }

        /**
         * Elimina una ubicación de la base de datos.
         *
         * @param location La ubicación a eliminar.
         */
        fun deleteLocation(location: Localizacion) {
            val em = getEntityManager()
            em.transaction.begin()
            em.remove(em.contains(location) ?: em.merge(location)) // Elimina la ubicación de la base de datos
            em.transaction.commit() // Confirma la transacción
            em.close() // Cierra el EntityManager
        }

        /**
         * Crea una nueva instancia de EntityManager para operaciones con la base de datos.
         *
         * @return EntityManager configurado.
         */
        private fun getEntityManager(): EntityManager {
            return EntityManagerFactoryInstance.entityManagerFactory!!.createEntityManager()
        }
    }
}

