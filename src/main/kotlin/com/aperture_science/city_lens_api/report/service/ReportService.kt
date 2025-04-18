package com.aperture_science.city_lens_api.report.service

import com.aperture_science.city_lens_api.location.repository.LocationRepository
import com.aperture_science.city_lens_api.report.controller.body.ReportCreateBody
import com.aperture_science.city_lens_api.report.controller.body.ReportOutputBody
import com.aperture_science.city_lens_api.report.controller.body.ReportUpdateBody
import com.aperture_science.city_lens_api.report.repository.ReportRepository
import com.aperture_science.city_lens_api.report.repository.entity.Reporte
import com.aperture_science.city_lens_api.user.repository.UsuarioRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class ReportService(
    private val reportRepository: ReportRepository,
    private val locationRepository: LocationRepository,
    private val usuarioRepository: UsuarioRepository
) {

    fun createReport(reportCreateBody: ReportCreateBody): ReportOutputBody {
        val usuario = usuarioRepository.getUserById(reportCreateBody.userId)
            ?: throw IllegalArgumentException("Usuario no encontrado con ID: ${reportCreateBody.userId}")

        val location = locationRepository.getLocationById(reportCreateBody.locationId)
            ?: throw IllegalArgumentException("Ubicación no encontrada con ID: ${reportCreateBody.locationId}")

        val reporte = Reporte(
            id = UUID.randomUUID(),
            usuario = usuario,
            title = reportCreateBody.title,
            description = reportCreateBody.description,
            status = reportCreateBody.status,
            location = location,
            creationDate = LocalDateTime.now(),
            resolutionDate = null,
            imageId = reportCreateBody.imageId
        )

        reportRepository.persistReport(reporte)

        return ReportOutputBody(
            id = reporte.id,
            title = reporte.title,
            description = reporte.description,
            status = reporte.status,
            locationId = reporte.location.id,
            creationDate = reporte.creationDate,
            resolutionDate = reporte.resolutionDate,
            imageId = reporte.imageId
        )
    }

    fun updateReport(id: UUID, reportUpdateBody: ReportUpdateBody): ReportOutputBody? {
        val existingReport = reportRepository.getReportById(id) ?: return null

        val updatedReport = existingReport.copy(
            title = reportUpdateBody.title ?: existingReport.title,
            description = reportUpdateBody.description ?: existingReport.description,
            status = reportUpdateBody.status ?: existingReport.status,
            resolutionDate = reportUpdateBody.resolutionDate ?: existingReport.resolutionDate
        )

        reportRepository.updateReport(existingReport, updatedReport)

        return ReportOutputBody(
            id = updatedReport.id,
            title = updatedReport.title,
            description = updatedReport.description,
            status = updatedReport.status,
            locationId = updatedReport.location.id,
            creationDate = updatedReport.creationDate,
            resolutionDate = updatedReport.resolutionDate,
            imageId = updatedReport.imageId
        )
    }

    fun deleteReport(id: UUID): Boolean {
        val report = reportRepository.getReportById(id) ?: return false
        reportRepository.deleteReport(report)
        return true
    }

    fun getReportById(id: UUID): ReportOutputBody? {
        val report = reportRepository.getReportById(id) ?: return null
        return ReportOutputBody(
            id = report.id,
            title = report.title,
            description = report.description,
            status = report.status,
            locationId = report.location.id,
            creationDate = report.creationDate,
            resolutionDate = report.resolutionDate,
            imageId = report.imageId
        )
    }
}