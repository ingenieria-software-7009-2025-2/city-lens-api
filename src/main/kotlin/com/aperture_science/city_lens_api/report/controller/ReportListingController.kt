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
        val reports = ReportService.listLatestReports(token)
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
        val reports = ReportService.listOldestReports(token)
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