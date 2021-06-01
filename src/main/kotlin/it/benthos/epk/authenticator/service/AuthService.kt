package it.benthos.epk.authenticator.service

import reactor.core.publisher.Mono

interface AuthService {
    fun getAuthSecret(): String
    fun login(username: String, password: String): Mono<String>
    fun upsertUser(username: String, password: String, secret: String): Mono<Void>
}