package com.aperture_science.city_lens_api.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("City Lens API - Una Aplicación de Reportes Urbanos")
                    .version("1.5.0")
                    .description(
                        """
                        _Un Programa de Aperture Science_

                        City Lens es un aplicación de reportes urbanos. En ella, los usuarios
                        pueden crear reportes sobre quejas del día a día en la ciudad, como baches,
                        coladeras sin tapa, manifestaciones, etc, esto proveyendo tanto dirección,
                        descripción y una imagen que acompañe al reporte.

                        El objetivo principal de City Lens es poder crear una sociedad más informada
                        sobre lo que acontece en su ciudad a nivel de desperfectos viales y que
                        pueda tomar medidas preventivas en su trayecto del día a día.
                        """.trimIndent()
                    )
                    .contact(
                        io.swagger.v3.oas.models.info.Contact()
                            .name("Equipo de Desarrollo de City Lens")
                            .email("edmont@ciencias.unam.mx")
                            .url("https://github.com/ingenieria-software-7009-2025-2/city-lens-api")
                    )
                    .license(
                        License()
                            .name("Apache 2.0")
                            .url("https//www.apache.org/licenses/LICENSE-2.0.html")
                    )

            )
    }

    @Bean
    fun publicApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("public")
            .pathsToMatch("/api/**")
            .build()
    }
}