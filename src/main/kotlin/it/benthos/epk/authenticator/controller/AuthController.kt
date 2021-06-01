package it.benthos.epk.authenticator.controller

import it.benthos.epk.authenticator.entity.User
import it.benthos.epk.authenticator.model.JwtResponse
import it.benthos.epk.authenticator.repository.UserRepository
import it.benthos.epk.authenticator.model.UserRequest
import it.benthos.epk.authenticator.security.PasswordEncoder
import it.benthos.epk.authenticator.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono


@CrossOrigin
@RestController
class AuthController(val authService: AuthService) {
    @PutMapping
    fun upsertUser(@RequestBody userRequest: UserRequest, @RequestHeader("x-secret") secret: String)
        = authService.upsertUser(userRequest.username, userRequest.password, secret)

    @PostMapping("/login")
    fun login(@RequestBody userRequest: UserRequest)
        = authService.login(userRequest.username, userRequest.password)
            .map { JwtResponse(it) }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Illegal arguments")
    @ExceptionHandler(IllegalArgumentException::class)
    fun illegalArgumentHandler() { }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Unknown error")
    @ExceptionHandler(Exception::class)
    fun unknownExceptionHandler(e: java.lang.Exception) = e.printStackTrace()
}