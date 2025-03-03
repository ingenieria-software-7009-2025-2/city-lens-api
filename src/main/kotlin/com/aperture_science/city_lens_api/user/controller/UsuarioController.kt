package com.aperture_science.city_lens_api.user.controller

import com.aperture_science.city_lens_api.UsuarioRegisterBody
import com.aperture_science.city_lens_api.user.controller.body.UsuarioLoginBody
import com.aperture_science.city_lens_api.user.controller.body.UsuarioLoginOutputBody
import com.aperture_science.city_lens_api.user.repository.entity.SessionToken
import com.aperture_science.city_lens_api.user.repository.entity.Usuario
import com.aperture_science.city_lens_api.util.EntityManagerFactoryInstance
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.aperture_science.city_lens_api.util.HashUtil
import jakarta.persistence.EntityManager
import java.util.UUID

@RestController
@RequestMapping
class UsuarioController {


    @PostMapping("/v1/users/login")
    fun Login(@RequestBody userCredentials: UsuarioLoginBody): ResponseEntity<UsuarioLoginOutputBody> {
        val em= getEntityManager()
        //query to database to get the user
        val loginUser = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario::class.java)
            .setParameter("email", userCredentials.email)
            .singleResult

        //autentication of the user, if the code passes this point, the password is correct
        if (loginUser.password != HashUtil.hash(userCredentials.password)) {
            em.close()
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        //create the token and return the answer
        val response = UsuarioLoginOutputBody(
            token= SessionToken.createToken(loginUser).token,
            user= loginUser
        )
        //Save the token in the database
        em.transaction.begin()
        em.persist(loginUser)
        em.transaction.commit()
        em.close()
        //finally return the response
        return ResponseEntity.ok(response)
    }
    @PostMapping("/v1/users/register")
    fun Register(@RequestBody userCredentials: UsuarioRegisterBody): ResponseEntity<Usuario> {
        val loginUser = Usuario(
            id= UUID.randomUUID(),
            firstName = userCredentials.first_name,
            lastName = userCredentials.last_name,
            email = userCredentials.email,
            password= HashUtil.hash(userCredentials.password),
        )
        //Due to possible multithreading, we need to create a new EntityManager instance
        val entityManager= getEntityManager()
        entityManager.transaction.begin()
        entityManager.persist(loginUser)
        entityManager.transaction.commit()
        entityManager.close()

        return ResponseEntity.ok(loginUser)
    }
    @PostMapping("/v1/users/logout")
    fun Logout(): ResponseEntity<String> {
        return ResponseEntity("Sesion cerrada", HttpStatus.OK)
    }
    @GetMapping("/v1/users/me")
    fun GetMyUser(): ResponseEntity<Usuario> {
        return ResponseEntity.ok( Usuario(
            firstName = "Paquito",
            email = "paquito@example.com",
            password="123456",
            id= UUID.randomUUID(),
        )
        )
    }
    private fun getEntityManager(): EntityManager {
        return EntityManagerFactoryInstance.entityManagerFactory!!.createEntityManager()
    }
}