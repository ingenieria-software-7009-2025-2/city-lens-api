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
class ReportController{

    @GetMapping("/test")
    fun testEndpoint(): ResponseEntity<String> {
        return ResponseEntity.ok("ReportController is working")
    }
    @PostMapping("/v1/report/create")
    fun createReport(@RequestBody reportCreateBody: ReportCreateBody, request: HttpServletRequest ): ResponseEntity<Any> {
        val token = request.getHeader("Authorization") ?: return ResponseEntity(
            null,
            HttpStatus.UNAUTHORIZED
        )
        val errorMessage = createRequestIsValid(reportCreateBody, token)
        if (errorMessage != null) {
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }
        val report = ReportService.createReport(reportCreateBody,token)
        return ResponseEntity(report, HttpStatus.CREATED)
    }
    @PostMapping ("/v1/report/update")
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
    private fun createRequestIsValid(reportCreateBody: ReportCreateBody, token:String ): String? {
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
        if (UsuarioRepository.getUserByToken(token)==null){
            return "Token invalido"
        }
        if (ReportRepository.LocationExists(reportCreateBody.latitude, reportCreateBody.longitude)) {
            return "El reporte ya existe"
        }
        else return null

    }
    private fun updateRequestIsValid(reportUpdateBody: ReportUpdateBody, token:String ): String? {
        if (reportUpdateBody.title!=null && reportUpdateBody.title.isEmpty()) {
            return "El titulo no puede estar vacio"
        }
        if (reportUpdateBody.description!=null && reportUpdateBody.description.isEmpty()) {
            return "La descripcion no puede estar vacia"
        }
        if (reportUpdateBody.status!=null && reportUpdateBody.status.isEmpty()) {
            return "El estado no puede estar vacio"
        }
        val report = ReportRepository.getReportById(reportUpdateBody.id)
        if (report == null) {
            return "El reporte no existe"
        }
        if (reportUpdateBody.resolutionDate!=null && reportUpdateBody.resolutionDate.isBefore(report.creationDate)) {
            return "La fecha de resolucion no puede ser anterior a la fecha de creacion"
        }
        val responsibleUser = UsuarioRepository.getUserByToken(token)
        if (responsibleUser == null) {
            return "Token invalido"
        }
        val authenticationToken = responsibleUser.id
        if (authenticationToken!=report.userUUID){
            return "No tienes permiso para modificar este reporte"
        }
        else return null
    }
    private fun deleteRequestIsValid(reportDeleteBody: ReportUpdateBody, token:String ): String? {
        val report = ReportRepository.getReportById(reportDeleteBody.id)
        if (report == null) {
            return "El reporte no existe"
        }
        val responsibleUser = UsuarioRepository.getUserByToken(token)
        if (responsibleUser == null ) {
            return "Token invalido"
        }
        val authenticationToken = responsibleUser.id
        if (authenticationToken!=report.userUUID&& responsibleUser.role == "User"){
            return "No tienes permiso para eliminar este reporte"
        }
        else return null
    }
}