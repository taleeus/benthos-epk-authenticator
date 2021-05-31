package it.benthos.epk.authenticator.controller

import it.benthos.epk.authenticator.entity.User
import it.benthos.epk.authenticator.repository.UserRepository
import it.benthos.epk.authenticator.request.UserRequest
import it.benthos.epk.authenticator.security.Authenticator
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono


@CrossOrigin
@RestController
class AuthController(val authenticator: Authenticator, val repo: UserRepository) {
    @PutMapping
    fun upsertUser(@RequestBody userRequest: UserRequest, @RequestHeader("x-secret") secret: String): Mono<Void> {
        if (secret != authenticator.secret) {
            return Mono.error { IllegalArgumentException("Unauthorized") }
        }

        val user = User(userRequest.username, authenticator.encodePassword(userRequest.password))
        return repo.save(user).then()
    }

    @PostMapping("/login")
    fun login(@RequestBody userRequest: UserRequest): Mono<Void> {
        return repo.findByUsername(userRequest.username)
            .filter { authenticator.passwordsMatch(userRequest.password, it.password) }
            .switchIfEmpty(Mono.error { IllegalArgumentException("Invalid login") })
            .then()
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Illegal arguments")
    @ExceptionHandler(IllegalArgumentException::class)
    fun illegalArgumentHandler() { }
}