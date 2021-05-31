package it.benthos.epk.authenticator.config

import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@EnableReactiveMongoRepositories
class MongoConfiguration : AbstractReactiveMongoConfiguration() {
    @Bean fun mongoClient() = MongoClients.create()

    override fun getDatabaseName() = "benthos"
    override fun autoIndexCreation() = true
}