package com.aperture_science.city_lens_api.report.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import com.aperture_science.city_lens_api.report.controller.body.ReportOutputBody
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import com.aperture_science.city_lens_api.report.service.ReportService
import com.aperture_science.city_lens_api.user.repository.UsuarioRepository
import com.aperture_science.city_lens_api.report.controller.body.ReportSearchBody
@RestController
@RequestMapping()
class ReportListingController {
    @GetMapping("/v1/list/latest")
    fun listLatestReports(request: HttpServletRequest): ResponseEntity<Any> {
        val token = request.getHeader("Authorization") ?: return ResponseEntity(
            null,
            HttpStatus.UNAUTHORIZED
        )
        val errorMessage = listRequestIsValid(token)
        if (errorMessage != null) {
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }
        val reports = ReportService.listLatestReports()
        return ResponseEntity(reports, HttpStatus.OK)
    }
    @GetMapping("/v1/list/oldest")
    fun listOldestReports(request: HttpServletRequest): ResponseEntity<Any> {
        val token = request.getHeader("Authorization") ?: return ResponseEntity(
            null,
            HttpStatus.UNAUTHORIZED
        )
        val errorMessage = listRequestIsValid(token)
        if (errorMessage != null) {
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }
        val reports = ReportService.listOldestReports()
        return ResponseEntity(reports, HttpStatus.OK)

    }
    @GetMapping("/v1/list/active")
    fun listActiveReports(request: HttpServletRequest): ResponseEntity<Any> {
        val token = request.getHeader("Authorization") ?: return ResponseEntity(
            null,
            HttpStatus.UNAUTHORIZED
        )
        val errorMessage = listRequestIsValid(token)
        if (errorMessage != null) {
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }
        val reports = ReportService.listActiveReports()
        return ResponseEntity(reports, HttpStatus.OK)
    }
    @GetMapping("/v1/list/recently-resolved")
    fun listRecentlyResolvedReports(request: HttpServletRequest): ResponseEntity<Any> {
        val token = request.getHeader("Authorization") ?: return ResponseEntity(
            null,
            HttpStatus.UNAUTHORIZED
        )
        val errorMessage = listRequestIsValid(token)
        if (errorMessage != null) {
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }
        val reports = ReportService.listRecentlyResolvedReports()
        return ResponseEntity(reports, HttpStatus.OK)
    }

    @GetMapping("/v1/list/by-zipcode")
    fun listReportsByZipcode(@RequestBody reportSearchBody: ReportSearchBody, request: HttpServletRequest): ResponseEntity<Any> {
        val token = request.getHeader("Authorization") ?: return ResponseEntity(
            null,
            HttpStatus.UNAUTHORIZED
        )
        var errorMessage = listRequestIsValid(token)
        if (errorMessage != null) {
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }
        if (reportSearchBody.zipcode==null){
            errorMessage = "El campo zipcode no puede ser nulo"
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }
        if (reportSearchBody.zipcode.isEmpty()) {
            errorMessage = "El campo zipcode no puede estar vacío"
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }
        if (reportSearchBody.zipcode.length != 5) {
            errorMessage = "El campo zipcode debe tener exactamente 5 caracteres"
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }
        val isAscending: Boolean
        if (reportSearchBody.ascending == null) {
            isAscending = false
        }else{
            isAscending= reportSearchBody.ascending.toBoolean()
        }

        val reports = ReportService.listReportsByZipcode(reportSearchBody.zipcode, isAscending)
        return ResponseEntity(reports, HttpStatus.OK)
    }


    fun listRequestIsValid(token: String): String? {
        val user = UsuarioRepository.getUserByToken(token)
        if (user == null) {
            return "Usuario no encontrado"
        }
        return null
    }

}