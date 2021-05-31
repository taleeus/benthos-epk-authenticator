package it.benthos.epk.authenticator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
class BenthosEpkAuthenticatorApplication

fun main(args: Array<String>) {
	runApplication<BenthosEpkAuthenticatorApplication>(*args)
}
