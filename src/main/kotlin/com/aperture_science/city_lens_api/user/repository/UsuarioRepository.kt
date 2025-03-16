package com.aperture_science.city_lens_api.user.repository
import com.aperture_science.city_lens_api.user.controller.body.UsuarioPutMeBody
import com.aperture_science.city_lens_api.user.repository.entity.SessionToken
import com.aperture_science.city_lens_api.util.EntityManagerFactoryInstance
import com.aperture_science.city_lens_api.user.repository.entity.Usuario
import jakarta.persistence.EntityManager
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID
import kotlin.collections.isNotEmpty

class UsuarioRepository {
    companion object {
        /**
         *
         *  Persiste un usuario en la base de datos
         *  @param user El usuario a persistir.
         */
        fun PersistUser(user: Usuario) {
            // Debido al posible multithreading, necesitamos crear una nueva instancia de EntityManager
            val em = getEntityManager()
            em.transaction.begin()
            em.persist(user)
            em.transaction.commit()
            em.close()


        }

        fun getUserByEmail(email: String): Usuario? {
            val em = getEntityManager()
            val user = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario::class.java)
                .setParameter("email", email)
                .singleResult
            em.close()
            return user
        }
        fun getUserToken(user: Usuario): SessionToken? {
            val em = getEntityManager()
            val token = em.createQuery("SELECT t FROM SessionToken t WHERE t.user = :userId", SessionToken::class.java)
                .setParameter("userId", user.id)
                .resultList
            em.close()
            if (token.isNotEmpty()) {
                return token[0]
            }
            return null
        }
        fun getSessionToken(token: String): SessionToken? {
            val em = getEntityManager()
            val sessionToken =
                em.createQuery("SELECT t FROM SessionToken t WHERE t.token = :token", SessionToken::class.java)
                    .setParameter("token", token)
                    .resultList
            em.close()
            if (sessionToken.isNotEmpty()) {
                return sessionToken[0]
            }
            return null
        }

        fun removeSessionToken(token: SessionToken) {
            val em = getEntityManager()
            em.transaction.begin()
            em.remove(token)
            em.transaction.commit()
            em.close()
        }
        fun getUserById(id: UUID): Usuario? {
            val em = getEntityManager()
            val user = em.createQuery("SELECT u FROM Usuario u WHERE u.id = :id", Usuario::class.java)
                .setParameter("id", id)
                .resultList
            em.close()
            if (user.isNotEmpty()) {
                return user[0]
            }
            return null
        }
        fun updateUsuario(user: Usuario, userChanges: UsuarioPutMeBody): Usuario {
            val em = getEntityManager()

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
            return updatedUser
        }
            fun getEntityManager(): EntityManager {
            return EntityManagerFactoryInstance.entityManagerFactory!!.createEntityManager()
        }

    }
}

