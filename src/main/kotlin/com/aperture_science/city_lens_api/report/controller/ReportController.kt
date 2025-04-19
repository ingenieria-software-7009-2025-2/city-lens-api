
import com.aperture_science.city_lens_api.report.controller.body.ReportCreateBody
import com.aperture_science.city_lens_api.report.controller.body.ReportOutputBody
import com.aperture_science.city_lens_api.report.controller.body.ReportUpdateBody
import com.aperture_science.city_lens_api.report.repository.entity.Location
import com.aperture_science.city_lens_api.report.service.ReportService
import com.aperture_science.city_lens_api.util.EntityManagerFactoryInstance
import com.aperture_science.city_lens_api.report.repository.ReportRepository
import jakarta.persistence.EntityManager
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
@RestController
@RequestMapping("/api")
class ReportController{
    @PostMapping("/v1/report/create")
    fun createReport(@RequestBody reportCreateBody: ReportCreateBody, request: HttpServletRequest ): ResponseEntity<Any> {
        val token = request.getHeader("Authorization") ?: return ResponseEntity(
            null,
            HttpStatus.UNAUTHORIZED
        )
        val errorMessage = createRequestIsValid(reportCreateBody)
        if (errorMessage != null) {
            return ResponseEntity(errorMessage, HttpStatus.BAD_REQUEST)
        }
        val report = ReportService.createReport(reportCreateBody,token)
        return ResponseEntity(report, HttpStatus.CREATED)
    }
    private fun createRequestIsValid(reportCreateBody: ReportCreateBody): String? {
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
        if (ReportRepository.LocationExists(reportCreateBody.latitude, reportCreateBody.longitude)) {
            return "El reporte ya existe"
        }
        else return null

    }
}