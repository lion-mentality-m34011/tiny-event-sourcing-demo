package ru.quipy.projections

import javassist.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.*
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct


@Component
class UserProjection (
    private val userRepository: UserRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    private val logger = LoggerFactory.getLogger(UserProjectsProjection::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "user::user-projection") {
            `when`(UserHasBeenCreatedEvent::class) { event ->
                userRepository.save(User(event.userId, event.login, event.password))
                logger.info("Create user ${event.userId}")
            }
        }
    }

    fun getByLoginAndPassword(login: String, password: String) : User? {
        val user = userRepository.findByLogin(login) ?: throw NotFoundException("User does not exists")
        if (user.password == password) {
            return user
        }
        throw NotFoundException("User with this password does not exists")
    }
}


@Document("user-projection")
data class User(
    @Id
    var userId: UUID,
    val login: String,
    val password: String,
)

@Repository
interface UserRepository : MongoRepository<User, UUID> {
    fun findByLogin(login: String) : User?
}