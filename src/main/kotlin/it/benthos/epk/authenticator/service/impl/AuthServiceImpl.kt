package it.benthos.epk.authenticator.service.impl

import it.benthos.epk.authenticator.entity.User
import it.benthos.epk.authenticator.repository.UserRepository
import it.benthos.epk.authenticator.security.JwtManager
import it.benthos.epk.authenticator.security.PasswordEncoder
import it.benthos.epk.authenticator.service.AuthService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthServiceImpl(val repo: UserRepository, val jwtManager: JwtManager) : AuthService {
    @Value("\${auth.secret}") lateinit var secret: String

    override fun getAuthSecret() = secret

    override fun login(username: String, password: String): Mono<String> {
        return repo.findByUsername(username)
            .filter { PasswordEncoder.passwordsMatch(password, it.password) }
            .switchIfEmpty(Mono.error { IllegalArgumentException("Invalid login") })
            .map { jwtManager.generateToken(it.username) }
    }

    override fun upsertUser(username: String, password: String, secret: String): Mono<Void> {
        if (secret != this.secret) {
            return Mono.error { IllegalArgumentException("Invalid secret") }
        }

        return repo.findByUsername(username)
            .switchIfEmpty(Mono.just(User(username, PasswordEncoder.encodePassword(password))))
            .map {
                it.password = password
                it
            }
            .flatMap { repo.save(it) }
            .then()
    }
}