package com.aperture_science.city_lens_api

import com.aperture_science.city_lens_api.util.EntityManagerFactoryInstance
import jakarta.persistence.Persistence
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CityLensApiApplication

/**
 * Función main del proyecto. Basta con inicializar el proyecto
 * desde esta ubicación en IntelliJ para poder ejecutarlo.
 */
fun main(args: Array<String>) {
    //Inicializa EntityManagerFactory.
    val emf = Persistence.createEntityManagerFactory("city_lens")

    //Establece EntityManagerFactory en EntityManagerFactoryInstance para facilitar el acceso.
    EntityManagerFactoryInstance.entityManagerFactory = emf
    runApplication<CityLensApiApplication>(*args)
}
