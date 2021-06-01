package it.benthos.epk.authenticator.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.HashMap

@Component
class JwtManager {
    @Value("\${jwt.secret}")
    private lateinit var secret: String

    @Value("\${jwt.expire-in}")
    private var expireIn: Long = 3600

    fun getUsernameFromToken(token: String) = getClaimFromToken<String>(token, Claims::getSubject)

    fun getIssuedAtDateFromToken(token: String) = getClaimFromToken<Date>(token, Claims::getIssuedAt)

    fun getExpirationDateFromToken(token: String) = getClaimFromToken<Date>(token, Claims::getExpiration)

    fun <T> getClaimFromToken(token: String, claimsResolver: (Claims) -> T): T {
        val claims: Claims = getAllClaimsFromToken(token)
        return claimsResolver(claims)
    }

    private fun getAllClaimsFromToken(token: String) = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody()

    private fun isTokenExpired(token: String): Boolean {
        val expiration: Date = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    private fun ignoreTokenExpiration(token: String) = false

    fun generateToken(username: String): String {
        val claims: MutableMap<String, Any> = HashMap()
        return doGenerateToken(claims, username)
    }

    private fun doGenerateToken(claims: Map<String, Any>, subject: String)
        = Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expireIn * 1000))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()

    fun canTokenBeRefreshed(token: String) = !isTokenExpired(token) || ignoreTokenExpiration(token)

}