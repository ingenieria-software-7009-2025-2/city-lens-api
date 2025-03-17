package com.aperture_science.city_lens_api.user.repository

import com.aperture_science.city_lens_api.user.controller.body.UsuarioPutMeBody
import com.aperture_science.city_lens_api.user.repository.entity.SessionToken
import com.aperture_science.city_lens_api.util.EntityManagerFactoryInstance
import com.aperture_science.city_lens_api.user.repository.entity.Usuario
import com.aperture_science.city_lens_api.util.HashUtil
import jakarta.persistence.EntityManager
import java.util.UUID
import kotlin.collections.isNotEmpty

/**
 * Repositorio para gestionar las operaciones de base de datos relacionadas con los usuarios.
 *
 * Esta clase proporciona métodos para interactuar con la base de datos, como guardar usuarios,
 * buscar usuarios por correo o ID, gestionar tokens de sesión y actualizar información de usuarios.
 */
class UsuarioRepository {
    companion object {

        /**
         * Persiste un usuario en la base de datos.
         *
         * @param user El usuario a persistir.
         */
        fun PersistUser(user: Usuario) {
            // Debido al posible multithreading, necesitamos crear una nueva instancia de EntityManager
            val em = getEntityManager()
            em.transaction.begin()
            em.persist(user) // Guarda el usuario en la base de datos
            em.transaction.commit() // Confirma la transacción
            em.close() // Cierra el EntityManager
        }
        fun PersistSessionToken(token: SessionToken) {
            val em = getEntityManager()
            em.transaction.begin()
            em.persist(token)
            em.transaction.commit()
            em.close()
        }

        /**
         * Busca un usuario por su correo electrónico.
         *
         * @param email El correo electrónico del usuario a buscar.
         * @return El usuario si se encuentra, o `null` si no existe.
         */
        fun getUserByEmail(email: String): Usuario? {
            val em = getEntityManager()
            // Ejecuta una consulta para buscar un usuario por su correo
            val user = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario::class.java)
                .setParameter("email", email)
                .singleResult
            em.close() // Cierra el EntityManager
            return user
        }

        /**
         * Obtiene el token de sesión asociado a un usuario.
         *
         * @param user El usuario del cual se busca el token.
         * @return El token de sesión si existe, o `null` si no hay tokens asociados.
         */
        fun getUserToken(user: Usuario): SessionToken? {
            val em = getEntityManager()
            // Ejecuta una consulta para buscar tokens asociados al usuario
            val token = em.createQuery("SELECT t FROM SessionToken t WHERE t.user = :userId", SessionToken::class.java)
                .setParameter("userId", user.id)
                .resultList
            em.close() // Cierra el EntityManager
            // Devuelve el primer token si existe, o `null` si no hay tokens
            return if (token.isNotEmpty()) token[0] else null
        }

        /**
         * Busca un token de sesión por su valor.
         *
         * @param token El valor del token a buscar.
         * @return El token de sesión si se encuentra, o `null` si no existe.
         */
        fun getSessionToken(token: String): SessionToken? {
            val em = getEntityManager()
            // Ejecuta una consulta para buscar un token por su valor
            val sessionToken =
                em.createQuery("SELECT t FROM SessionToken t WHERE t.token = :token", SessionToken::class.java)
                    .setParameter("token", token)
                    .resultList
            em.close() // Cierra el EntityManager
            // Devuelve el primer token si existe, o `null` si no hay tokens
            return if (sessionToken.isNotEmpty()) sessionToken[0] else null
        }

        /**
         * Elimina un token de sesión de la base de datos.
         *
         * @param token El token de sesión a eliminar.
         */
        fun removeSessionToken(token: SessionToken) {
            val em = getEntityManager()
            // Todo este codigote es porque se requiere que el valor sea obtenido por el mismo entitymanager
            em.transaction.begin()
            val user = getUserById(token.user)!!
            // Ejecuta una consulta para buscar tokens asociados al usuario
            val token = em.createQuery("SELECT t FROM SessionToken t WHERE t.user = :userId", SessionToken::class.java)
                .setParameter("userId", user.id)
                .resultList
            em.remove(token[0]) // Elimina el token de la base de datos
            em.transaction.commit() // Confirma la transacción
            em.close() // Cierra el EntityManager
        }

        /**
         * Busca un usuario por su ID.
         *
         * @param id El ID del usuario a buscar.
         * @return El usuario si se encuentra, o `null` si no existe.
         */
        fun getUserById(id: UUID): Usuario? {
            val em = getEntityManager()
            // Ejecuta una consulta para buscar un usuario por su ID
            val user = em.createQuery("SELECT u FROM Usuario u WHERE u.id = :id", Usuario::class.java)
                .setParameter("id", id)
                .resultList
            em.close() // Cierra el EntityManager
            // Devuelve el primer usuario si existe, o `null` si no hay usuarios
            return if (user.isNotEmpty()) user[0] else null
        }

        /**
         * Actualiza la información de un usuario en la base de datos.
         *
         * @param user El usuario con la información actual.
         * @param userChanges Los cambios a aplicar al usuario.
         * @return El usuario actualizado.
         */
        fun updateUsuario(user: Usuario, userChanges: UsuarioPutMeBody): Usuario {
            val em = getEntityManager()

            // Crea una copia del usuario con los cambios aplicados
            val updatedUser = user.copy(
                firstName = userChanges.first_name ?: user.firstName,
                lastName = userChanges.last_name ?: user.lastName,
                email = userChanges.email ?: user.email,
                password = userChanges.password ?: user.password
            )
            updatedUser.password = HashUtil.hash(updatedUser.password)
            em.transaction.begin()
            em.merge(updatedUser) // Actualiza el usuario en la base de datos
            em.transaction.commit() // Confirma la transacción
            em.close() // Cierra el EntityManager

            return updatedUser
        }

        /**
         * Crea una nueva instancia de EntityManager para operaciones con la base de datos.
         *
         * @return EntityManager configurado.
         */
        private fun getEntityManager(): EntityManager {
            return EntityManagerFactoryInstance.entityManagerFactory!!.createEntityManager()
        }
    }
}