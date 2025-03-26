package com.aperture_science.city_lens_api.user.service

import com.aperture_science.city_lens_api.user.repository.entity.Usuario
import com.aperture_science.city_lens_api.UsuarioRegisterBody
import com.aperture_science.city_lens_api.user.controller.body.UsuarioLoginOutputBody
import com.aperture_science.city_lens_api.user.controller.body.UsuarioLoginOutputUser
import com.aperture_science.city_lens_api.user.controller.body.UsuarioPutMeBody
import com.aperture_science.city_lens_api.user.repository.UsuarioRepository
import com.aperture_science.city_lens_api.user.repository.entity.SessionToken
import com.aperture_science.city_lens_api.util.HashUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID


class UsuarioService {
    companion object{
        /**
         * Genera un nuevo token de sesión para un usuario.
         *
         * @param user Usuario a registrar
         * @return [com.aperture_science.city_lens_api.user.repository.entity.Usuario] con token hasheado basado en email + timestamp
         */
        fun RegisterUser(user: UsuarioRegisterBody): Usuario  {
            val loginUser = Usuario(
                id = UUID.randomUUID(),
                firstName = user.first_name,
                lastName = user.last_name,
                email = user.email,
                password = HashUtil.hash(user.password),
            )
            UsuarioRepository.PersistUser(loginUser)
            return loginUser;

        }
        /**
        * Autentica a un usuario y genera un token de sesión.
        * @param email Email del usuario
        * @param password Contraseña del usuario
        * @return Respuesta con token de sesión y datos del usuario
         */
        fun LoginUser(email: String, password: String): ResponseEntity<Any?> {
            val user = UsuarioRepository.getUserByEmail(email)
            //No se puede construir la respuesta fuera debido a que se utilizan distintos codigos de error.
            when(ValidateLogin(email, password)){

                0 -> {
                    println("Login correcto")

                    val UserResponse = UsuarioLoginOutputBody(
                        token = SessionToken.createToken(user!!).token,
                        user = UsuarioLoginOutputUser(
                            id = user.id,
                            email = user.email,
                            first_name = user.firstName,
                            last_name = user.lastName,
                            role = user.role
                        )
                    )
                    UsuarioRepository.PersistSessionToken(SessionToken.createToken(user))
                    return ResponseEntity.ok(UserResponse)
                }// Contraseña incorrecta
                1 -> {
                    println("Contraseña incorrecta")
                    return ResponseEntity.status(401).build()
                }
                // Usuario ya tiene una sesión activa
                2 -> {
                    println("Usuario ya tiene una sesión activa")
                    // Eliminar la sesión activa anterior
                    val activeToken = UsuarioRepository.getUserToken(user!!)
                    if (activeToken != null) {
                        UsuarioRepository.removeSessionToken(activeToken)
                    }
                    // Crear una nueva sesión
                    val newToken = SessionToken.createToken(user)
                    UsuarioRepository.PersistSessionToken(newToken)

                    val UserResponse = UsuarioLoginOutputBody(
                        token = newToken.token,
                        user = UsuarioLoginOutputUser(
                            id = user.id,
                            email = user.email,
                            first_name = user.firstName,
                            last_name = user.lastName,
                            role = user.role
                        )
                    )
                    return ResponseEntity.ok(UserResponse)
                }
                else -> {
                    // Esto no debería pasar, pero si pasa, devolvemos un error 500.
                    return ResponseEntity.status(500).build()
                }
            }

        }

        /**
         * Cierra la sesión de un usuario eliminando su token.
         * @param token Token de sesión del usuario
         * @return Código de validación
         * 0 -> Logout correcto
         * 1 -> Token no encontrado
         */
        fun LogoutUser(token: String): Int {
            val sessionToken = UsuarioRepository.getSessionToken(token)
            if (sessionToken != null) {
                UsuarioRepository.removeSessionToken(sessionToken)
                return 0
            }
            return 1
        }

        /**
         * Obtiene los datos de un usuario a partir de un token de sesión.
         * @param token Token de sesión del usuario
         * @return Datos del usuario
         * null -> Token o Usuario no encontrado
         * Usuario -> Datos del usuario
         */
        fun getMe(token: String): Usuario? {
            val sessionToken = UsuarioRepository.getSessionToken(token)
            if (sessionToken == null) {
                return null
            }
            return UsuarioRepository.getUserById(sessionToken.user)
        }

        fun postMe(token: String, userChanges: UsuarioPutMeBody): Usuario? {
            val sessionToken = UsuarioRepository.getSessionToken(token)
            if (sessionToken == null) {
                return null
            }
            val user = UsuarioRepository.getUserById(sessionToken.user)
            return UsuarioRepository.updateUsuario(user!!, userChanges)
        }
        /**
        * Valida el login de un usuario
        * @param email Email del usuario
        * @param password Contraseña del usuario
        * @return Código de validación
        * 0 -> Sin conflictos
        * 1 -> Contraseña incorrecta O Usuario no encontrado
        * 2 -> Usuario ya tiene una sesión activa
         */
        private fun ValidateLogin(email: String, password: String): Int {
            val user = UsuarioRepository.getUserByEmail(email)
            return if (user != null && user.password == HashUtil.hash(password)) {
                if (UsuarioRepository.getUserToken(user) == null) {
                    0
                }else{
                    2
                }
            }else{
                1
            }
        }
    }
}