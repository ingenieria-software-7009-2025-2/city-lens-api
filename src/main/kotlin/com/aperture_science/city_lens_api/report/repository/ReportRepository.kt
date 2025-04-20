package com.aperture_science.city_lens_api.report.repository

import com.aperture_science.city_lens_api.report.repository.entity.Location
import com.aperture_science.city_lens_api.report.repository.entity.Reporte
import com.aperture_science.city_lens_api.report.repository.entity.Image
import com.aperture_science.city_lens_api.user.repository.entity.SessionToken
import com.aperture_science.city_lens_api.util.EntityManagerFactoryInstance
import jakarta.persistence.EntityManager
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID
import kotlin.reflect.typeOf

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
            /*
         *  Abandonad toda esperanza aquellos que osen adentrarse en este código,
         *  porque aquí yace la desesperación y el caos absoluto 💀💀.
         *
         *  En un intento de eliminar un reporte, me encontré atrapado en un laberinto de errores y frustraciones,
         *  y durante dos horas de puro sufrimiento, luché contra la misteriosa desaparición de location_id
         *  en un Report row mientras intentaba su eliminación. Springboot, en su infinita misericordia,
         *  decidió arrojarme un error porque, aparentemente, el tipo "Int" de Kotlin le resultaba insufrible.
         *
         *  ¡Pero esperad! La alternativa, "Integer", tampoco fue bienvenida en tierras de Kotlin,
         *  y así comenzó un descenso en espiral hacia el abismo de soluciones desesperadas.
         *
         *  Al final, en un acto de absoluta rendición, me vi forzado a abandonar toda noción
         *  de elegancia y buenas prácticas. En lugar de persistir, decidí ejecutar una consulta de datos
         *  directa para borrar la información manualmente en esta función, sellando mi destino como
         *  un ser atormentado por malas decisiones y soluciones indignas.
         *
         *  Si algún alma valiente llega hasta aquí, que sepa que este código es un monumento al
         *  dolor de desarrollo y una advertencia para las generaciones futuras:
         *  A veces, el código limpio es un lujo imposible.
         */
            val em = getEntityManager()
            em.transaction.begin()
            println("Location ID: ${report.locationID}")
            if (report.imageId!=null){
                val image = getImageById(report.imageId!!)
                deleteImage(image!!) // Elimina la imagen de la base de datos
            }
            println("Location ID: ${report.locationID}")
            val location= getLocationById(report.locationID)
            deleteLocation(location!!) // Elimina la ubicación de la base de datos
            em.createQuery("DELETE FROM Reporte r WHERE r.id = :id")
                .setParameter("id", report.id)
                .executeUpdate()
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
        fun getImageById(id: UUID): Image? {
            val em = getEntityManager()
            val image = em.createQuery("SELECT i FROM Image i WHERE i.id = :id", Image::class.java)
                .setParameter("id", id)
                .resultList
            em.close() // Cierra el EntityManager
            return if (image.isNotEmpty()) image[0] else null
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
            em.createQuery("DELETE FROM Location l WHERE l.id = :id")
                .setParameter("id", location.locationId)
                .executeUpdate()
            em.transaction.commit() // Confirma la transacción
            em.close() // Cierra el EntityManager
        }
        fun deleteImage(image: Image) {
            val em = getEntityManager()
            em.transaction.begin()
            val attachedImage = em.createQuery("SELECT i FROM Image i WHERE i.id = :id", Image::class.java)
                .setParameter("id", image.id)
                .resultList
            em.remove(attachedImage[0]) // Elimina la imagen de la base de datos
            em.transaction.commit() // Confirma la transacción
            em.close() // Cierra el EntityManager
        }
        fun listLatestReports(): List<Reporte> {
            val em = getEntityManager()
            val reports = em.createNativeQuery("SELECT * FROM get_latest_reports()", Reporte::class.java)
                .resultList
            em.close() // Cierra el EntityManager
            if (reports.isEmpty()) {
                return emptyList()
            }
            return reports as List<Reporte>
        }
        fun listOldestReports(): List<Reporte> {
            val em = getEntityManager()
            val reports = em.createNativeQuery("SELECT * FROM get_oldest_reports()", Reporte::class.java)
                .resultList
            em.close() // Cierra el EntityManager
            if (reports.isEmpty()) {
                return emptyList()
            }
            return reports as List<Reporte>
        }
    }
}