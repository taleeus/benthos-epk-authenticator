package it.benthos.epk.authenticator.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

object PasswordEncoder {
    fun passwordsMatch(password: String, storedPassword: String) = Hasher.sha256(password) == storedPassword
    fun encodePassword(password: String) = Hasher.sha256(password)
}