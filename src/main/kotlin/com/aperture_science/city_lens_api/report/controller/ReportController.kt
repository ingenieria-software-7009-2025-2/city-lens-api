package com.aperture_science.city_lens_api.report.controller

import com.aperture_science.city_lens_api.report.controller.body.ReportCreateBody
import com.aperture_science.city_lens_api.report.controller.body.ReportUpdateBody
import com.aperture_science.city_lens_api.report.service.ReportService
import com.aperture_science.city_lens_api.report.repository.ReportRepository
import com.aperture_science.city_lens_api.user.repository.UsuarioRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping()
class ReportController {

    @GetMapping("/test")
    fun testEndpoint(): ResponseEntity<String> {
        return ResponseEntity.ok("ReportController is working")
    }

    @PostMapping("/v1/report/create")
    fun createReport(@RequestBody reportCreateBody: ReportCreateBody, request: HttpServletRequest): ResponseEntity<Any> {
        val token = request.getHeader("Authorization") ?: return ResponseEntity(
            null,
            HttpStatus.UNAUTHORIZED
        )
        val errorMessage = createRequestIsValid(reportCreateBody, token)
        if (errorMessage != null) {
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }
        val report = ReportService.createReport(reportCreateBody, token)
        return ResponseEntity(report, HttpStatus.CREATED)
    }

    @PostMapping("/v1/report/update")
    fun updateReport(@RequestBody reportUpdateBody: ReportUpdateBody, request: HttpServletRequest): ResponseEntity<Any> {
        val token = request.getHeader("Authorization") ?: return ResponseEntity(
            null,
            HttpStatus.UNAUTHORIZED
        )
        val errorMessage = updateRequestIsValid(reportUpdateBody, token)
        if (errorMessage != null) {
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }
        val report = ReportService.updateReport(reportUpdateBody)
        return ResponseEntity(report, HttpStatus.OK)
    }

    @PostMapping("/v1/report/delete")
    fun deleteReport(@RequestBody reportDeleteBody: ReportUpdateBody, request: HttpServletRequest): ResponseEntity<Any> {
        val token = request.getHeader("Authorization") ?: return ResponseEntity(
            null,
            HttpStatus.UNAUTHORIZED
        )
        val errorMessage = deleteRequestIsValid(reportDeleteBody, token)
        if (errorMessage != null) {
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }
        ReportService.deleteReport(reportDeleteBody.id)
        return ResponseEntity(null, HttpStatus.OK)
    }

    private fun createRequestIsValid(reportCreateBody: ReportCreateBody, token: String): String? {
        if (reportCreateBody.latitude < -90 || reportCreateBody.latitude > 90) {
            return "Latitud invalida"
        }
        if (reportCreateBody.longitude < -180 || reportCreateBody.longitude > 180) {
            return "Longitud invalida"
        }
        if (reportCreateBody.title.isEmpty()) {
            return "El titulo no puede estar vacio"
        }
        if (reportCreateBody.description.isEmpty()) {
            return "La descripcion no puede estar vacia"
        }
        if (reportCreateBody.municipality.isEmpty()) {
            return "El municipio no puede estar vacio"
        }
        if (reportCreateBody.zipcode.isEmpty()) {
            return "El codigo postal no puede estar vacio"
        }
        if (UsuarioRepository.getUserByToken(token) == null) {
            return "Token invalido"
        }
        if (ReportRepository.LocationExists(reportCreateBody.latitude, reportCreateBody.longitude)) {
            return "El reporte ya existe"
        }
        return null
    }

    private fun updateRequestIsValid(reportUpdateBody: ReportUpdateBody, token: String): String? {
        if (reportUpdateBody.title != null && reportUpdateBody.title.isEmpty()) {
            return "El titulo no puede estar vacio"
        }
        if (reportUpdateBody.description != null && reportUpdateBody.description.isEmpty()) {
            return "La descripcion no puede estar vacia"
        }
        if (reportUpdateBody.status != null && reportUpdateBody.status.isEmpty()) {
            return "El estado no puede estar vacio"
        }
        val report = ReportRepository.getReportById(reportUpdateBody.id)
        if (report == null) {
            return "El reporte no existe"
        }
        if (reportUpdateBody.resolutionDate != null && reportUpdateBody.resolutionDate.isBefore(report.creationDate)) {
            return "La fecha de resolucion no puede ser anterior a la fecha de creacion"
        }
        val responsibleUser = UsuarioRepository.getUserByToken(token)
        if (responsibleUser == null) {
            return "Token invalido"
        }
        val authenticationToken = responsibleUser.id
        if (authenticationToken != report.userUUID) {
            return "No tienes permiso para modificar este reporte"
        }
        return null
    }

    private fun deleteRequestIsValid(reportDeleteBody: ReportUpdateBody, token: String): String? {
        val report = ReportRepository.getReportById(reportDeleteBody.id)
        if (report == null) {
            return "El reporte no existe"
        }
        val responsibleUser = UsuarioRepository.getUserByToken(token)
        if (responsibleUser == null) {
            return "Token invalido"
        }
        val authenticationToken = responsibleUser.id
        if (authenticationToken != report.userUUID && responsibleUser.role == "User") {
            return "No tienes permiso para eliminar este reporte"
        }
        return null
    }
}