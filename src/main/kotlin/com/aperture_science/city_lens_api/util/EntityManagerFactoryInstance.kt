package com.aperture_science.city_lens_api.util

import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.EntityManager
class EntityManagerFactoryInstance {
    companion object{
        var entityManagerFactory: EntityManagerFactory? = null
            get() = field
            set(value) {
                field = value
            }



    }
}