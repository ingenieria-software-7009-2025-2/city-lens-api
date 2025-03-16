package com.aperture_science.city_lens_api.user.controller

import com.aperture_science.city_lens_api.UsuarioRegisterBody
import com.aperture_science.city_lens_api.user.controller.body.UsuarioLoginBody
import com.aperture_science.city_lens_api.user.controller.body.UsuarioLoginOutputBody
import com.aperture_science.city_lens_api.user.controller.body.UsuarioPutMeBody
import com.aperture_science.city_lens_api.user.repository.entity.SessionToken
import com.aperture_science.city_lens_api.user.repository.entity.Usuario
import com.aperture_science.city_lens_api.user.service.UsuarioService

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
    fun Login(@RequestBody userCredentials: UsuarioLoginBody): ResponseEntity<Any?> {
        val loginUser = UsuarioService.LoginUser(userCredentials.email, userCredentials.password)
        return ResponseEntity.ok(loginUser)
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param userCredentials Datos del nuevo usuario
     * @return Usuario registrado
     */
    @PostMapping("/v1/users/register")
    fun Register(@RequestBody userCredentials: UsuarioRegisterBody): ResponseEntity<Usuario> {

        val loginUser = UsuarioService.RegisterUser(userCredentials)

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
        if (token == null) {
            return ResponseEntity("Se requiere un Token de Autenticación", HttpStatus.UNAUTHORIZED)
        }
        val logoutStatus =  UsuarioService.LogoutUser(token)
        return when(logoutStatus){
            0 -> {
                ResponseEntity.ok("Sesión cerrada")
            }

            1 -> {
                ResponseEntity("Token de autorización no valido", HttpStatus.UNAUTHORIZED)
            }

            else -> {
                ResponseEntity("Error interno", HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
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
        return when(val user = UsuarioService.getMe(token)){
            null -> {
                ResponseEntity(null, HttpStatus.UNAUTHORIZED)
            }
            else -> {
                ResponseEntity.ok(user)
            }
        }
    }

    /**
     * Crea una nueva instancia de EntityManager para operaciones con la base de datos.
     *
     * @return EntityManager configurado
     */
    @PutMapping("/v1/users/me")
    fun PostMyUser(@RequestBody userChanges: UsuarioPutMeBody, request: HttpServletRequest): ResponseEntity<Usuario> {
        val token = request.getHeader("Authorization")
        val updatedUser = UsuarioService.postMe(token, userChanges)
        // Si el token no corresponde a un usuario, devolvemos un error 401
        when(updatedUser){
            null -> {
                return ResponseEntity(null, HttpStatus.UNAUTHORIZED)
            }
        }
        // Si el usuario fue actualizado correctamente, devolvemos el usuario actualizado
        return ResponseEntity.ok(updatedUser)
    }

    private fun getEntityManager(): EntityManager {
        return EntityManagerFactoryInstance.entityManagerFactory!!.createEntityManager()
    }
}
