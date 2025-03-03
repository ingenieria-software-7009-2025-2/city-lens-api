package com.aperture_science.city_lens_api.user.controller

import com.aperture_science.city_lens_api.UsuarioRegisterBody
import com.aperture_science.city_lens_api.user.controller.body.UsuarioLoginBody
import com.aperture_science.city_lens_api.user.controller.body.UsuarioLoginOutputBody
import com.aperture_science.city_lens_api.user.repository.entity.SessionToken
import com.aperture_science.city_lens_api.user.repository.entity.Usuario
import com.aperture_science.city_lens_api.util.EntityManagerFactoryInstance
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import jakarta.servlet.http.HttpServletRequest
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
        // Check if the user already has an active session
        val existingToken = em.createQuery("SELECT t FROM SessionToken t WHERE t.user.id = :userId", SessionToken::class.java)
            .setParameter("userId", loginUser.id)
            .resultList
        if (existingToken.isNotEmpty()) {
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
        em.persist(response.token)
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
    fun Logout(request:HttpServletRequest): ResponseEntity<String> {
        val token = request.getHeader("Authorization")
        val em= getEntityManager()
        val sessionToken = em.createQuery("SELECT t FROM SessionToken t WHERE t.token = :token",SessionToken::class.java)
            .setParameter("token", token)
            .resultList
        if (sessionToken.isEmpty()) {
            em.close()
            return ResponseEntity("Token no encontrado", HttpStatus.NOT_FOUND)
        }
        em.transaction.begin()
        em.remove(sessionToken[0])
        em.transaction.commit()
        em.close()
        return ResponseEntity("Sesion cerrada", HttpStatus.OK)
    }
    @GetMapping("/v1/users/me")
    fun GetMyUser(request:HttpServletRequest): ResponseEntity<Usuario> {
        val token = request.getHeader("Authorization")
        val em= getEntityManager()
        val sessionToken = em.createQuery("SELECT t FROM SessionToken t WHERE t.token = :token",SessionToken::class.java)
            .setParameter("token", token)
            .resultList
        if (sessionToken.isEmpty()) {
            em.close()
            return ResponseEntity<Usuario>(null, HttpStatus.UNAUTHORIZED)
        }
        val userid = sessionToken[0].userID
        val user= em.createQuery("SELECT u FROM Usuario u WHERE u.id = :id",Usuario::class.java)
            .setParameter("id", userid)
            .singleResult
        em.close()
        return ResponseEntity.ok(user)
    }
    @PostMapping("/v1/users/me")
    fun PostMyUser(request:HttpServletRequest): ResponseEntity<Usuario> {
        val token = request.getHeader("Authorization")
        val em= getEntityManager()
        val sessionToken = em.createQuery("SELECT t FROM SessionToken t WHERE t.token = :token",SessionToken::class.java)
            .setParameter("token", token)
            .resultList
        if (sessionToken.isEmpty()) {
            em.close()
            return ResponseEntity(null, HttpStatus.UNAUTHORIZED)
        }
        val userid = sessionToken[0].userID
        val user= em.createQuery("SELECT u FROM Usuario u WHERE u.id = :id",Usuario::class.java)
            .setParameter("id", userid)
            .singleResult
        val updatedUser = user.copy(
            firstName = request.getParameter("firstName") ?: user.firstName,
            lastName = request.getParameter("lastName") ?: user.lastName,
            email = request.getParameter("email") ?: user.email,
            password = request.getParameter("password")?.let { HashUtil.hash(it) } ?: user.password
        )
        em.transaction.begin()
        em.merge(updatedUser)
        em.transaction.commit()
        em.close()
        return ResponseEntity.ok(updatedUser)
    }
    private fun getEntityManager(): EntityManager {
        return EntityManagerFactoryInstance.entityManagerFactory!!.createEntityManager()
    }
}