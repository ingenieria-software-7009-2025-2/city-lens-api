package com.aperture_science.city_lens_api.report.controller.body



/**
 * Clase que representa la información necesaria para buscar reportes por código postal.
 */
data class ReportSearchBody(
    val zipcode: String? = null, // Código postal por el cual se desea buscar reportes
    val ascending: String? = null, // Indica si el orden es ascendente o descendente (true/false)
)