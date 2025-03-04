package com.aperture_science.city_lens_api.user.controller

import com.aperture_science.city_lens_api.UsuarioRegisterBody
import com.aperture_science.city_lens_api.user.controller.body.UsuarioLoginBody
import com.aperture_science.city_lens_api.user.controller.body.UsuarioLoginOutputBody
import com.aperture_science.city_lens_api.user.controller.body.UsuarioPutMeBody
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

/**
 * Controlador para gestionar las operaciones de autenticación y perfil de usuario.
 *
 * Expone endpoints para login, registro, logout y gestión de datos del usuario.
 */
@RestController
@RequestMapping
class UsuarioController {

    /**
     * Auténtica a un usuario y genera un token de sesión.
     *
     * @param userCredentials Credenciales de login (email y contraseña)
     * @return Respuesta con token de sesión y datos del usuario
     */
    @PostMapping("/v1/users/login")
    fun Login(@RequestBody userCredentials: UsuarioLoginBody): ResponseEntity<UsuarioLoginOutputBody> {
        val em = getEntityManager()
        val loginUser = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario::class.java)
            .setParameter("email", userCredentials.email)
            .singleResult

        // Autenticación del usuario, si el código pasa de este punto, la contraseña es correcta
        if (loginUser.password != HashUtil.hash(userCredentials.password)) {
            em.close()
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        // Comprobar si el usuario ya tiene una sesión activa
        val existingToken =
            em.createQuery("SELECT t FROM SessionToken t WHERE t.user = :userId", SessionToken::class.java)
                .setParameter("userId", loginUser.id)
                .resultList
        if (existingToken.isNotEmpty()) {
            em.close()
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val sessionToken = SessionToken.createToken(loginUser)
        // Creación del token
        val response = UsuarioLoginOutputBody(
            token = sessionToken.token,
            user = loginUser
        )

        // Almacena el token en la DB
        em.transaction.begin()
        em.persist(sessionToken)
        em.transaction.commit()
        em.close()
        // Finalmente, se regresa la respuesta (Se espera sea un 200 OK)
        return ResponseEntity.ok(response)
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param userCredentials Datos del nuevo usuario
     * @return Usuario registrado
     */
    @PostMapping("/v1/users/register")
    fun Register(@RequestBody userCredentials: UsuarioRegisterBody): ResponseEntity<Usuario> {
        val loginUser = Usuario(
            id = UUID.randomUUID(),
            firstName = userCredentials.first_name,
            lastName = userCredentials.last_name,
            email = userCredentials.email,
            password = HashUtil.hash(userCredentials.password),
        )
        // Debido al posible multithreading, necesitamos crear una nueva instancia de EntityManager
        val entityManager = getEntityManager()
        entityManager.transaction.begin()
        entityManager.persist(loginUser)
        entityManager.transaction.commit()
        entityManager.close()

        return ResponseEntity.ok(loginUser)
    }

    /**
     * Cierra la sesión de un usuario eliminando su token.
     *
     * @param request Solicitud HTTP con el token en el header "Authorization"
     * @return Mensaje de confirmación
     */
    @PostMapping("/v1/users/logout")
    fun Logout(request: HttpServletRequest): ResponseEntity<String> {
        val token = request.getHeader("Authorization")
        val em = getEntityManager()
        val sessionToken =
            em.createQuery("SELECT t FROM SessionToken t WHERE t.token = :token", SessionToken::class.java)
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
        return ResponseEntity("Sesión cerrada", HttpStatus.OK)
    }

    /**
     * Obtiene los datos del usuario autenticado.
     *
     * @param request Solicitud HTTP con el token en el header "Authorization"
     * @return Datos del usuario
     */
    @GetMapping("/v1/users/me")
    fun GetMyUser(request: HttpServletRequest): ResponseEntity<Usuario> {
        val token = request.getHeader("Authorization")
        val em = getEntityManager()
        val sessionToken =
            em.createQuery("SELECT t FROM SessionToken t WHERE t.token = :token", SessionToken::class.java)
                .setParameter("token", token)
                .resultList
        if (sessionToken.isEmpty()) {
            em.close()
            return ResponseEntity<Usuario>(null, HttpStatus.UNAUTHORIZED)
        }
        val userid = sessionToken[0].user
        val user = em.createQuery("SELECT u FROM Usuario u WHERE u.id = :id", Usuario::class.java)
            .setParameter("id", userid)
            .singleResult
        em.close()
        return ResponseEntity.ok(user)
    }

    /**
     * Crea una nueva instancia de EntityManager para operaciones con la base de datos.
     *
     * @return EntityManager configurado
     */
    @PutMapping("/v1/users/me")
    fun PostMyUser(@RequestBody userChanges: UsuarioPutMeBody, request: HttpServletRequest): ResponseEntity<Usuario> {
        val token = request.getHeader("Authorization")
        val em = getEntityManager()
        val sessionToken =
            em.createQuery("SELECT t FROM SessionToken t WHERE t.token = :token", SessionToken::class.java)
                .setParameter("token", token)
                .resultList
        if (sessionToken.isEmpty()) {
            em.close()
            return ResponseEntity(null, HttpStatus.UNAUTHORIZED)
        }
        val userid = sessionToken[0].user
        val user = em.createQuery("SELECT u FROM Usuario u WHERE u.id = :id", Usuario::class.java)
            .setParameter("id", userid)
            .singleResult
        val updatedUser = user.copy(
            firstName = userChanges.first_name ?: user.firstName,
            lastName = userChanges.last_name ?: user.lastName,
            email = userChanges.email ?: user.email,
            password = userChanges.password ?: user.password
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
