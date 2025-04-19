package com.aperture_science.city_lens_api.report.repository

import com.aperture_science.city_lens_api.report.repository.entity.Reporte
import com.aperture_science.city_lens_api.util.EntityManagerFactoryInstance
import jakarta.persistence.EntityManager
import java.util.UUID

/**
 * Repositorio para gestionar las operaciones de base de datos relacionadas con los reportes.
 *
 * Esta clase proporciona métodos para interactuar con la base de datos, como guardar reportes,
 * buscar reportes por ID, actualizar información de reportes y eliminarlos.
 */
class ReportRepository {
    companion object {

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
                location = updatedReport.location,
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