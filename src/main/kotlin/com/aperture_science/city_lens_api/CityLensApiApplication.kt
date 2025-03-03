package com.aperture_science.city_lens_api

import com.aperture_science.city_lens_api.util.EntityManagerFactoryInstance
import jakarta.persistence.Persistence
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CityLensApiApplication

fun main(args: Array<String>) {
	//Starts the Entity Manager Factory
	val emf = Persistence.createEntityManagerFactory("city_lens");
	//Sets the Entity Manager Factory in the EntityManagerFactoryInstance utility class for easy access
	EntityManagerFactoryInstance.entityManagerFactory = emf;
	runApplication<CityLensApiApplication>(*args)
}
