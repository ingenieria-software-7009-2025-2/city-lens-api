package com.aperture_science.city_lens_api.report.repository

import com.aperture_science.city_lens_api.report.repository.entity.Location
import com.aperture_science.city_lens_api.report.repository.entity.Reporte
import com.aperture_science.city_lens_api.report.repository.entity.Image
import com.aperture_science.city_lens_api.util.EntityManagerFactoryInstance
import jakarta.persistence.EntityManager
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

/**
 * Repositorio para gestionar las operaciones de base de datos relacionadas con los reportes.
 *
 * Esta clase proporciona métodos para interactuar con la base de datos, como guardar reportes,
 * buscar reportes por ID, actualizar información de reportes y eliminarlos.
 */
class ReportRepository {
    companion object {
        fun persistLocation(location: Location):Int{
            val em = getEntityManager()
            em.transaction.begin()
            em.persist(location) // Guarda la ubicación en la base de datos
            em.flush() // Asegura que la ubicación genere su ID
            val locationId = location.locationId
            em.transaction.commit() // Confirma la transacción
            em.close() // Cierra el EntityManager
            return locationId
        }
        /**
         * Persiste un reporte en la base de datos.
         *
         * @param report El reporte a persistir.
         */
        fun persistReport(report: Reporte) {
            val em = getEntityManager()
            em.transaction.begin()
            em.persist(report) // Guarda el reporte en la base de datos
            em.transaction.commit() // Confirma la transacción
            em.close() // Cierra el EntityManager
        }

        /**
         * Busca un reporte por su ID.
         *
         * @param id El ID del reporte a buscar.
         * @return El reporte si se encuentra, o `null` si no existe.
         */
        fun getReportById(id: UUID): Reporte? {
            val em = getEntityManager()
            val report = em.createQuery("SELECT r FROM Reporte r WHERE r.id = :id", Reporte::class.java)
                .setParameter("id", id)
                .resultList
            em.close() // Cierra el EntityManager
            return if (report.isNotEmpty()) report[0] else null
        }

        /**
         * Actualiza la información de un reporte en la base de datos.
         *
         * @param report El reporte con la información actual.
         * @param updatedReport Los cambios a aplicar al reporte.
         * @return El reporte actualizado.
         */
        fun updateReport(report: Reporte, updatedReport: Reporte): Reporte {
            val em = getEntityManager()
            val updated = report.copy(
                title = updatedReport.title,
                description = updatedReport.description,
                status = updatedReport.status,
                locationID = updatedReport.locationID,
                resolutionDate = updatedReport.resolutionDate
            )

            em.transaction.begin()
            em.merge(updated) // Actualiza el reporte en la base de datos
            em.transaction.commit() // Confirma la transacción
            em.close() // Cierra el EntityManager

            return updated
        }

        /**
         * Elimina un reporte de la base de datos.
         *
         * @param report El reporte a eliminar.
         */
        fun deleteReport(report: Reporte) {
            val em = getEntityManager()
            em.transaction.begin()
            em.remove(em.contains(report) ?: em.merge(report)) // Elimina el reporte de la base de datos
            em.transaction.commit() // Confirma la transacción
            em.close() // Cierra el EntityManager
        }
        fun LocationExists(latitude: Double, longitude: Double): Boolean {
            val em = getEntityManager()
            // Redondea la latitud y longitud a 2 decimales
            val latitudeRounded = BigDecimal(latitude).setScale(2, RoundingMode.HALF_UP)
            val longitudeRounded = BigDecimal(longitude).setScale(2, RoundingMode.HALF_UP)
            val location = em.createQuery(
                "SELECT l FROM Location l WHERE l.latitude = :latitude AND l.longitude = :longitude",
                Location::class.java
            )
                .setParameter("latitude", latitude)
                .setParameter("longitude", longitude)
                .resultList
            em.close() // Cierra el EntityManager
            return location.isNotEmpty()

        }

        /**
         * Crea una nueva instancia de EntityManager para operaciones con la base de datos.
         *
         * @return EntityManager configurado.
         */
        private fun getEntityManager(): EntityManager {
            return EntityManagerFactoryInstance.entityManagerFactory!!.createEntityManager()

        }
        /**
         * Busca una ubicación por su ID.
         *
         * @param id El ID de la ubicación a buscar.
         * @return La ubicación si se encuentra, o `null` si no existe.
         */
        fun getLocationById(id: Int): Location? {
            val em = getEntityManager()
            val location = em.createQuery("SELECT l FROM Location l WHERE l.id = :id", Location::class.java)
                .setParameter("id", id)
                .resultList
            em.close() // Cierra el EntityManager
            return if (location.isNotEmpty()) location[0] else null
        }
        fun persistImage(image: Image):UUID {
            val em = getEntityManager()
            em.transaction.begin()
            em.persist(image) // Guarda la imagen en la base de datos
            em.flush() // Asegura que la imagen genere su ID
            val imageId = image.id
            em.transaction.commit() // Confirma la transacción
            em.close() // Cierra el EntityManager
            return imageId
        }
        /**
         * Elimina una ubicación de la base de datos.
         *
         * @param location La ubicación a eliminar.
         */
        fun deleteLocation(location: Location) {
            val em = getEntityManager()
            em.transaction.begin()
            em.remove(em.contains(location) ?: em.merge(location)) // Elimina la ubicación de la base de datos
            em.transaction.commit() // Confirma la transacción
            em.close() // Cierra el EntityManager
        }
    }
}