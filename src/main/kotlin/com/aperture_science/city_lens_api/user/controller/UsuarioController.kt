package com.aperture_science.city_lens_api.user.controller

import com.aperture_science.city_lens_api.UsuarioRegisterBody
import com.aperture_science.city_lens_api.user.controller.body.UsuarioLoginBody
import com.aperture_science.city_lens_api.user.controller.body.UsuarioPutMeBody
import com.aperture_science.city_lens_api.user.repository.entity.Usuario
import com.aperture_science.city_lens_api.user.service.UsuarioService

import com.aperture_science.city_lens_api.util.EntityManagerFactoryInstance
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.persistence.EntityManager
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controlador para gestionar las operaciones de autenticación y perfil de usuario.
 *
 * Expone endpoints para login, registro, logout y gestión de datos del usuario.
 */
@RestController
@RequestMapping
@Tag(name = "Usuarios", description = "Operaciones de autenticación y gestión de perfil de usuario")
class UsuarioController {

    /**
     * Auténtica a un usuario y genera un token de sesión.
     *
     * @param userCredentials Credenciales de login (email y contraseña)
     * @return Respuesta con token de sesión y datos del usuario
     */
    @PostMapping("/v1/users/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica a un usuario y devuelve un token de sesión.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del usuario previamente registrado",
            required = true,
            content = [io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Autenticación exitosa",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioLoginBody::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Credenciales inválidas",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain",
                )]
            )
        ]
    )
    fun Login(@RequestBody userCredentials: UsuarioLoginBody): ResponseEntity<Any?> {
        return UsuarioService.LoginUser(userCredentials.email, userCredentials.password)
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param userCredentials Datos del nuevo usuario
     * @return Usuario registrado
     */
    @PostMapping("/v1/users/register")
    @Operation(
        summary = "Registrar un nuevo usuario",
        description = "Crea una nueva cuenta de usuario con los datos proporcionados.",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del nuevo usuario",
            required = true,
            content = [io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Usuario registrado correctamente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioRegisterBody::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Datos de Registro Inválidos",
                // Aquí en particular se repite el esquema, en el entendido de que se
                // puede devolver un mensaje de error con el mismo formato, solo que
                // con los campos vacíos.
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioRegisterBody::class)
                )]
            )
        ],
    )
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
    @Operation(
        summary = "Cerrar sesión",
        description = "Elimina el token de sesión del usuario autenticado.",
        security = [SecurityRequirement(name = "bearerAuth")],
        parameters = [
            Parameter(name = "Autenticación", description = "Token de autenticación", required = true),
        ],
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del usuario a cerrar sesión.",
            required = true,
            content = [io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Sesión cerrada correctamente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain",
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Token de autenticación inválido o ausente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain",
                )]
            )
        ]
    )
    fun Logout(request: HttpServletRequest): ResponseEntity<String> {
        val token = request.getHeader("Authorization") ?: return ResponseEntity(
            "Se requiere un Token de Autenticación",
            HttpStatus.UNAUTHORIZED
        )
        return when (UsuarioService.LogoutUser(token)) {
            0 -> ResponseEntity.ok("Sesión cerrada")
            1 -> ResponseEntity("Token de autorización no válido", HttpStatus.UNAUTHORIZED)
            else -> ResponseEntity("Error interno", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * Obtiene los datos del usuario autenticado.
     *
     * @param request Solicitud HTTP con el token en el header "Authorization"
     * @return Datos del usuario
     */
    @GetMapping("/v1/users/me")
    @Operation(
        summary = "Obtener datos del usuario autenticado",
        description = "Devuelve la información del usuario autenticado.",
        security = [SecurityRequirement(name = "bearerAuth")],
        parameters = [
            Parameter(name = "Autenticación", description = "Token de autenticación", required = true),
        ],
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Formato de salida de los datos del usuario",
            required = true,
            content = [io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioPutMeBody::class)
            )]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Datos del usuario obtenidos correctamente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioPutMeBody::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Token de autenticación inválido",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain",
                )]
            )
        ]
    )
    fun GetMyUser(request: HttpServletRequest): ResponseEntity<Usuario> {
        val token = request.getHeader("Authorization")
        return when (val user = UsuarioService.getMe(token)) {
            null -> ResponseEntity(null, HttpStatus.UNAUTHORIZED)
            else -> ResponseEntity.ok(user)
        }
    }

    /**
     * Actualiza los datos del usuario autenticado.
     *
     * @param request Solicitud HTTP con el token en el header "Authorization"
     * @param userChanges Datos a actualizar
     * @return Datos actualizados del usuario
     */
    @PutMapping("/v1/users/me")
    @Operation(
        summary = "Actualizar datos del usuario autenticado",
        description = "Permite modificar la información del usuario autenticado.",
        security = [SecurityRequirement(name = "bearerAuth")],
        parameters = [
            Parameter(name = "Autenticación", description = "Token de autenticación", required = true),
        ],
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Formato a usar para actualizar los datos del usuario",
            required = true,
            content = [io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json")]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Usuario actualizado correctamente",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioPutMeBody::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Token de autenticación inválido",
                content = [io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "text/plain",
                )]
            )
        ]
    )
    fun PostMyUser(@RequestBody userChanges: UsuarioPutMeBody, request: HttpServletRequest): ResponseEntity<Usuario> {
        val token = request.getHeader("Authorization")
        val updatedUser = UsuarioService.postMe(token, userChanges)
        return if (updatedUser == null) {
            ResponseEntity(null, HttpStatus.UNAUTHORIZED)
        } else {
            ResponseEntity.ok(updatedUser)
        }
    }

    /**
     * Crea una nueva instancia de EntityManager para operaciones con la base de datos.
     *
     * @return EntityManager configurado
     */
    private fun getEntityManager(): EntityManager {
        return EntityManagerFactoryInstance.entityManagerFactory!!.createEntityManager()
    }
}
