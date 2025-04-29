package com.aperture_science.city_lens_api.report.service

import com.aperture_science.city_lens_api.report.controller.body.ReportCreateBody
import com.aperture_science.city_lens_api.report.controller.body.ReportOutputBody
import com.aperture_science.city_lens_api.report.controller.body.ReportUpdateBody
import com.aperture_science.city_lens_api.report.repository.ReportRepository
import com.aperture_science.city_lens_api.report.repository.ReportRepository.Companion.getLocationById
import com.aperture_science.city_lens_api.report.repository.entity.Image
import com.aperture_science.city_lens_api.report.repository.entity.Location
import com.aperture_science.city_lens_api.report.repository.entity.Reporte
import com.aperture_science.city_lens_api.user.repository.UsuarioRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class ReportService{
    companion object {

        /**
         * Crea un nuevo reporte en la base de datos a partir de los datos proporcionados.
         *
         * Esta función registra la ubicación y opcionalmente una imagen asociada al reporte,
         * además de vincularlo al usuario que realizó la creación.
         *
         * @param reportCreateBody Datos necesarios para crear el reporte.
         * @param token Token de autenticación del usuario que realiza el reporte.
         * @return Un objeto [ReportOutputBody] que representa el reporte creado.
         */
        fun createReport(reportCreateBody: ReportCreateBody, token: String ): ReportOutputBody {
            // Obtiene el usuario asociado al token de autenticación
            val user = UsuarioRepository.getUserByToken(token) ?: throw Exception("User not found")
            
            // Prepara la entidad Location con los datos del reporte
            val location = Location (
                locationId = 0, //Debido a que el ID es autogenerado, se asigna un valor temporal
                latitude = reportCreateBody.latitude,
                longitude = reportCreateBody.longitude,
                direction = reportCreateBody.direction,
                zipcode = reportCreateBody.zipcode,
                municipality = reportCreateBody.municipality
            )
            // Persiste la ubicación y obtiene su ID generado
            val locationId = ReportRepository.persistLocation(location)
            
            // Manejo opcional de la imagen del reporte
            val imageId: UUID? = null
            if(reportCreateBody.imageURL != null) {
                // Genera un UUID único para la imagen
                val imageId = UUID.randomUUID()
                val image = Image(
                    id = imageId,
                    imageURL = reportCreateBody.imageURL
                )
            }
            
            // Crea la entidad Reporte con todos los datos
            val reporte = Reporte(
                id = UUID.randomUUID(), // Genera un ID único para el reporte
                userUUID = user.id, // Asocia el reporte al usuario
                title = reportCreateBody.title,
                description = reportCreateBody.description,
                status = "open", // Estado inicial por defecto
                locationID = locationId, // ID de la ubicación persistida
                creationDate = LocalDateTime.now(), // Fecha actual del sistema
                resolutionDate = null, // Inicialmente no tiene fecha de resolución
                imageId = imageId // Puede ser null si no hay imagen
            )
            ReportRepository.persistReport(reporte)

            // Retorna el DTO con los datos del reporte creado
            return ReportOutputBody(
                id = reporte.id,
                title = reporte.title,
                description = reporte.description,
                status = reporte.status,
                location = location,
                creationDate = reporte.creationDate,
                resolutionDate = reporte.resolutionDate,
                imageId = reporte.imageId
            )
        }

        /**
         * Actualiza un reporte existente en la base de datos con los nuevos valores proporcionados.
         *
         * Esta función permite actualizar campos como título, descripción, estado y fecha de resolución.
         *
         * @param reportUpdateBody Contiene los datos actualizados para el reporte.
         * @return El reporte actualizado como un [ReportOutputBody], o `null` si no se encontró el reporte original.
         */
        fun updateReport(reportUpdateBody: ReportUpdateBody): ReportOutputBody? {
            // Obtiene el reporte existente o retorna null si no existe
            val existingReport = ReportRepository.getReportById(reportUpdateBody.id) ?: return null
            val location = getLocationById(existingReport.locationID)
            
            // Crea una copia del reporte con los campos actualizados
            val updatedReport = existingReport.copy(
                title = reportUpdateBody.title ?: existingReport.title, // Mantiene el valor actual si no se proporciona uno nuevo
                description = reportUpdateBody.description ?: existingReport.description,
                status = reportUpdateBody.status ?: existingReport.status,
                resolutionDate = reportUpdateBody.resolutionDate ?: existingReport.resolutionDate
            )

            // Persiste los cambios en la base de datos
            ReportRepository.updateReport(existingReport, updatedReport)

            // Retorna el DTO con los datos actualizados
            return ReportOutputBody(
                id = updatedReport.id,
                title = updatedReport.title,
                description = updatedReport.description,
                status = updatedReport.status,
                location = location!!, // !! ya que locationID es una FK existente
                creationDate = updatedReport.creationDate,
                resolutionDate = updatedReport.resolutionDate,
                imageId = updatedReport.imageId
            )
        }

        /**
         * Elimina un reporte de la base de datos usando su identificador único.
         *
         * @param id Identificador único del reporte a eliminar.
         * @return `true` si el reporte fue eliminado exitosamente, `false` si no se encontró.
         */
        fun deleteReport(id: UUID): Boolean {
            // Busca el reporte y retorna false si no existe
            val report = ReportRepository.getReportById(id) ?: return false
            // Delega la eliminación al repositorio
            ReportRepository.deleteReport(report)
            return true
        }

        /**
         * Obtiene un reporte específico de la base de datos usando su identificador.
         *
         * @param id Identificador único del reporte.
         * @return El reporte encontrado o `null` si no existe.
         */
        fun getReportById(id: UUID): Reporte? {
            // Delega la búsqueda al repositorio
            val report = ReportRepository.getReportById(id) ?: return null
            return report
        }

        /**
         * Lista los reportes más recientes registrados en el sistema.
         *
         * @return Una lista de objetos [ReportOutputBody] ordenados de más reciente a más antiguo.
         */
        fun listLatestReports(): List<ReportOutputBody> {
            // Obtiene los reportes más recientes del repositorio
            val reports = ReportRepository.listLatestReports()
            var reportOutput = mutableListOf<ReportOutputBody>()
            
            // Convierte cada entidad Reporte a su DTO correspondiente
            for (report in reports) {
                val location = getLocationById(report.locationID)
                reportOutput.add(
                    ReportOutputBody(
                        id = report.id,
                        title = report.title,
                        description = report.description,
                        status = report.status,
                        location = location!!, // !! ya que locationID es una FK existente
                        creationDate = report.creationDate,
                        resolutionDate = report.resolutionDate,
                        imageId = report.imageId
                    )
                )
            }
            return reportOutput
        }

        /**
         * Lista los reportes más antiguos registrados en el sistema.
         *
         * @return Una lista de objetos [ReportOutputBody] ordenados de más antiguo a más reciente.
         */
        fun listOldestReports(): List<ReportOutputBody> {
            // Obtiene los reportes más antiguos del repositorio
            val reports = ReportRepository.listOldestReports()
            var reportOutput = mutableListOf<ReportOutputBody>()
            
            // Convierte cada entidad Reporte a su DTO correspondiente
            for (report in reports) {
                val location = getLocationById(report.locationID)
                reportOutput.add(
                    ReportOutputBody(
                        id = report.id,
                        title = report.title,
                        description = report.description,
                        status = report.status,
                        location = location!!,
                        creationDate = report.creationDate,
                        resolutionDate = report.resolutionDate,
                        imageId = report.imageId
                    )
                )
            }
            return reportOutput
        }

        /**
         * Lista los reportes que actualmente están activos (no resueltos).
         *
         * @return Una lista de objetos [ReportOutputBody] de reportes abiertos.
         */
        fun listActiveReports(): List<ReportOutputBody> {
            // Obtiene los reportes activos del repositorio
            val reports = ReportRepository.listActiveReports()
            var reportOutput = mutableListOf<ReportOutputBody>()
            
            // Convierte cada entidad Reporte a su DTO correspondiente
            for (report in reports) {
                val location = getLocationById(report.locationID)
                reportOutput.add(
                    ReportOutputBody(
                        id = report.id,
                        title = report.title,
                        description = report.description,
                        status = report.status,
                        location = location!!,
                        creationDate = report.creationDate,
                        resolutionDate = report.resolutionDate,
                        imageId = report.imageId
                    )
                )
            }
            return reportOutput
        }

        /**
         * Lista los reportes que fueron resueltos recientemente.
         *
         * @return Una lista de objetos [ReportOutputBody] de reportes cerrados recientemente.
         */
        fun listRecentlyResolvedReports(): List<ReportOutputBody> {
            // Obtiene los reportes resueltos recientemente del repositorio
            val reports = ReportRepository.listRecentlyResolvedReports()
            var reportOutput = mutableListOf<ReportOutputBody>()
            
            // Convierte cada entidad Reporte a su DTO correspondiente
            for (report in reports) {
                val location = getLocationById(report.locationID)
                reportOutput.add(
                    ReportOutputBody(
                        id = report.id,
                        title = report.title,
                        description = report.description,
                        status = report.status,
                        location = location!!,
                        creationDate = report.creationDate,
                        resolutionDate = report.resolutionDate,
                        imageId = report.imageId
                    )
                )
            }
            return reportOutput
        }

        /**
         * Lista los reportes filtrados por código postal, en orden ascendente o descendente.
         *
         * @param zipcode Código postal por el cual filtrar los reportes.
         * @param ascending Indica si el orden debe ser ascendente (`true`) o descendente (`false`).
         * @return Una lista de objetos [ReportOutputBody] correspondientes al código postal.
         */
        fun listReportsByZipcode(zipcode: String, ascending: Boolean): List<ReportOutputBody> {
            // Obtiene los reportes filtrados por código postal
            val reports = ReportRepository.listReportsByZipcode(zipcode, ascending)
            var reportOutput = mutableListOf<ReportOutputBody>()
            
            // Convierte cada entidad Reporte a su DTO correspondiente
            for (report in reports) {
                val location = getLocationById(report.locationID)
                reportOutput.add(
                    ReportOutputBody(
                        id = report.id,
                        title = report.title,
                        description = report.description,
                        status = report.status,
                        location = location!!,
                        creationDate = report.creationDate,
                        resolutionDate = report.resolutionDate,
                        imageId = report.imageId
                    )
                )
            }
            return reportOutput
        }
    }
}