package com.aperture_science.city_lens_api.report.controller

import com.aperture_science.city_lens_api.report.controller.body.ReportOutputBody
import com.aperture_science.city_lens_api.report.controller.body.ReportCreateBody
import com.aperture_science.city_lens_api.report.controller.body.ReportDeleteBody
import com.aperture_science.city_lens_api.report.controller.body.ReportUpdateBody
import com.aperture_science.city_lens_api.report.controller.body.ReportSearchBody
import com.aperture_science.city_lens_api.report.service.ReportService
import com.aperture_science.city_lens_api.report.repository.ReportRepository
import com.aperture_science.city_lens_api.user.repository.UsuarioRepository
import jakarta.servlet.http.HttpServletRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping
@Tag(name = "Reportes", description = "Operaciones para crear, actualizar, eliminar y buscar reportes de incidentes en el sistema")
class ReportController {
    /**
     * Crea un nuevo reporte en el sistema.
     *
     * @param reportCreateBody Datos necesarios para crear el reporte.
     * @param request Solicitud HTTP que contiene el token de autenticación en el header "Authorization".
     * @return El reporte creado o un mensaje de error si la solicitud es inválida.
     */
    @PostMapping("/v1/report/create")
    @Operation(
        summary = "Crear un nuevo reporte",
        description = "Registra un nuevo reporte de incidente en el sistema con la información proporcionada.",
        security = [SecurityRequirement(name = "bearerAuth")],
        parameters = [
            Parameter(name = "Authorization", description = "Token de autenticación", required = true)
        ],
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos requeridos para crear un reporte",
            required = true,
            content = [io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = io.swagger.v3.oas.annotations.media.Schema(implementation = ReportCreateBody::class)
            )]
        ),
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Reporte creado exitosamente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = io.swagger.v3.oas.annotations.media.Schema(implementation = ReportOutputBody::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Datos del reporte inválidos o incompletos",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain"
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Token de autenticación inválido o ausente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain"
                )]
            )
        ]
    )
    fun createReport(
        @RequestBody reportCreateBody: ReportCreateBody,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        val token = request.getHeader("Authorization") ?: return ResponseEntity(
            "Se requiere un Token de Autenticación",
            HttpStatus.UNAUTHORIZED
        )

        val errorMessage = createRequestIsValid(reportCreateBody, token)
        if (errorMessage != null) {
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }

        val report = ReportService.createReport(reportCreateBody, token)
        return ResponseEntity(report, HttpStatus.CREATED)
    }


    /**
     * Actualiza la información de un reporte existente.
     *
     * @param reportUpdateBody Datos a actualizar del reporte.
     * @param request Solicitud HTTP con el token en el header "Authorization".
     * @return Reporte actualizado o mensaje de error si ocurre algún problema.
     */
    @PutMapping("/v1/report/update")
    @Operation(
        summary = "Actualizar un reporte existente",
        description = "Permite modificar los campos de un reporte previamente creado.",
        security = [SecurityRequirement(name = "bearerAuth")],
        parameters = [
            Parameter(name = "Authorization", description = "Token de autenticación", required = true)
        ],
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos para actualizar el reporte",
            required = true,
            content = [io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = io.swagger.v3.oas.annotations.media.Schema(implementation = ReportUpdateBody::class)
            )]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Reporte actualizado exitosamente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = io.swagger.v3.oas.annotations.media.Schema(implementation = ReportOutputBody::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Datos de actualización inválidos",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain"
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Token de autenticación inválido o ausente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain"
                )]
            )
        ]
    )
    fun updateReport(@RequestBody reportUpdateBody: ReportUpdateBody, request: HttpServletRequest): ResponseEntity<Any> {
        val token = request.getHeader("Authorization") ?: return ResponseEntity(
            "Se requiere un Token de Autenticación",
            HttpStatus.UNAUTHORIZED
        )

        val errorMessage = updateRequestIsValid(reportUpdateBody, token)
        if (errorMessage != null) {
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }

        val report = ReportService.updateReport(reportUpdateBody)
        return ResponseEntity(report, HttpStatus.OK)
    }



    /**
     * Elimina un reporte existente de la base de datos.
     *
     * @param reportDeleteBody Contiene el ID del reporte a eliminar.
     * @param request Solicitud HTTP con el token en el header "Authorization".
     * @return Confirmación de eliminación o mensaje de error.
     */
    @DeleteMapping("/v1/report/delete")
    @Operation(
        summary = "Eliminar un reporte",
        description = "Elimina un reporte de la base de datos utilizando su ID.",
        security = [SecurityRequirement(name = "bearerAuth")],
        parameters = [
            Parameter(name = "Authorization", description = "Token de autenticación", required = true)
        ],
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "ID del reporte a eliminar",
            required = true,
            content = [io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = io.swagger.v3.oas.annotations.media.Schema(implementation = ReportDeleteBody::class)
            )]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Reporte eliminado correctamente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain"
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Solicitud de eliminación inválida",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain"
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Token de autenticación inválido o ausente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain"
                )]
            )
        ]
    )
    fun deleteReport(@RequestBody reportDeleteBody: ReportDeleteBody, request: HttpServletRequest): ResponseEntity<Any> {
        val token = request.getHeader("Authorization") ?: return ResponseEntity(
            "Se requiere un Token de Autenticación",
            HttpStatus.UNAUTHORIZED
        )

        val errorMessage = deleteRequestIsValid(reportDeleteBody, token)
        if (errorMessage != null) {
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }

        ReportService.deleteReport(UUID.fromString(reportDeleteBody.id))
        return ResponseEntity(null, HttpStatus.OK)
    }

    /**
     * Busca reportes filtrados por código postal.
     *
     * @param reportSearchBody Contiene el código postal y el orden de búsqueda.
     * @param request Solicitud HTTP con el token en el header "Authorization".
     * @return Lista de reportes filtrados o mensaje de error.
     */
    @GetMapping("/v1/report/search")
    @Operation(
        summary = "Buscar reportes por código postal",
        description = "Permite buscar reportes filtrándolos por código postal y ordenarlos ascendente o descendente.",
        security = [SecurityRequirement(name = "bearerAuth")],
        parameters = [
            Parameter(name = "Authorization", description = "Token de autenticación", required = true)
        ],
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Código postal y orden de búsqueda para los reportes",
            required = true,
            content = [io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = io.swagger.v3.oas.annotations.media.Schema(implementation = ReportSearchBody::class)
            )]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Reportes obtenidos exitosamente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = io.swagger.v3.oas.annotations.media.Schema(implementation = ReportOutputBody::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Parámetros de búsqueda inválidos",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain"
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Token de autenticación inválido o ausente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain"
                )]
            )
        ]
    )
    fun searchReports(@RequestBody reportSearchBody: ReportSearchBody, request: HttpServletRequest): ResponseEntity<Any> {
        val token = request.getHeader("Authorization") ?: return ResponseEntity(
            "Se requiere un Token de Autenticación",
            HttpStatus.UNAUTHORIZED
        )

        val errorMessage = searchRequestIsValid(reportSearchBody, token)
        if (errorMessage != null) {
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }

        val ascending = reportSearchBody.ascending?.toBooleanStrictOrNull() ?: false
        val reports = ReportService.listReportsByZipcode(reportSearchBody.zipcode!!, ascending)

        return ResponseEntity(reports, HttpStatus.OK)
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

    private fun deleteRequestIsValid(reportDeleteBody: ReportDeleteBody, token: String): String? {
        val report = ReportRepository.getReportById(UUID.fromString(reportDeleteBody.id))
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



    private fun searchRequestIsValid(reportSearchBody: ReportSearchBody, token: String): String? {
        if (reportSearchBody.zipcode.isNullOrEmpty()) {
            return "El código postal no puede estar vacío"
        }
        if (reportSearchBody.zipcode.length != 5) {
            return "El código postal debe tener 5 dígitos"
        }
        if (reportSearchBody.ascending != null && reportSearchBody.ascending.lowercase() !in listOf("true", "false")) {
            return "El campo ascending debe ser 'true' o 'false'"
        }
        val responsibleUser = UsuarioRepository.getUserByToken(token)
        if (responsibleUser == null) {
            return "Token invalido"
        }
        return null
    }

}