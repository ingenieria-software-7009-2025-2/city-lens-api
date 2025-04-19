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

        fun createReport(reportCreateBody: ReportCreateBody, Token: String ): ReportOutputBody {
            val user = UsuarioRepository.getUserByToken(Token) ?: throw Exception("User not found")
            val location = Location (
                locationId = 0, //Debido a que el ID es autogenerado, se asigna un valor temporal
                latitude = reportCreateBody.latitude,
                longitude = reportCreateBody.longitude,
                direction = reportCreateBody.direction,
                zipcode = reportCreateBody.zipcode,
                municipality = reportCreateBody.municipality
            )
            val locationId = ReportRepository.persistLocation(location)
            val imageId: UUID? = null
            if(reportCreateBody.imageURL != null) {
                val imageId = UUID.randomUUID()
                val image = Image(
                    id = imageId,
                    imageURL = reportCreateBody.imageURL
                )
            }
            val reporte = Reporte(
                id = UUID.randomUUID(),
                userUUID = user.id,
                title = reportCreateBody.title,
                description = reportCreateBody.description,
                status = "Active",
                locationID = locationId,
                creationDate = LocalDateTime.now(),
                resolutionDate = null,
                imageId = imageId
            )
            ReportRepository.persistReport(reporte)

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

        fun updateReport(id: UUID, reportUpdateBody: ReportUpdateBody): ReportOutputBody? {
            val existingReport = ReportRepository.getReportById(id) ?: return null
            val location = getLocationById(existingReport.locationID)
            val updatedReport = existingReport.copy(
                title = reportUpdateBody.title ?: existingReport.title,
                description = reportUpdateBody.description ?: existingReport.description,
                status = reportUpdateBody.status ?: existingReport.status,
                resolutionDate = reportUpdateBody.resolutionDate ?: existingReport.resolutionDate
            )

            ReportRepository.updateReport(existingReport, updatedReport)

            return ReportOutputBody(
                id = updatedReport.id,
                title = updatedReport.title,
                description = updatedReport.description,
                status = updatedReport.status,
                location = location!!,
                creationDate = updatedReport.creationDate,
                resolutionDate = updatedReport.resolutionDate,
                imageId = updatedReport.imageId
            )
        }

        fun deleteReport(id: UUID): Boolean {
            val report = ReportRepository.getReportById(id) ?: return false
            ReportRepository.deleteReport(report)
            return true
        }

        fun getReportById(id: UUID): Reporte? {
            val report = ReportRepository.getReportById(id) ?: return null
            return report
        }
    }
}