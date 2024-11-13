package ru.quipy.projections

import javassist.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.*
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct


@Component
class UserProjectsProjection (
    private val userProjectsRepository: UserProjectsRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    private val logger = LoggerFactory.getLogger(UserProjectsProjection::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "user::user-project-projection") {
            `when`(UserHasBeenCreatedEvent::class) { event ->
                userProjectsRepository.save(UserProjects(event.userId, mutableListOf()))
                logger.info("Create user projects ${event.userId}")
            }
        }
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project::user-project-projection") {
            `when`(UserHasBeenAddedEvent::class) { event ->
                var task = userProjectsRepository.findByIdOrNull(event.userId) ?: throw NotFoundException("User does not exists")
                task.projects.add(event.projectId)
                userProjectsRepository.save(task)
                logger.info("Add project to user projects ${event.userId}")
            }
        }
    }

    fun getById(projectId: UUID) : UserProjects? {
        return userProjectsRepository.findByIdOrNull(projectId)
    }
}


@Document("user-project-projection")
data class UserProjects(
    @Id
    var userId: UUID,
    val projects: MutableList<UUID> = mutableListOf()
)

@Repository
interface UserProjectsRepository : MongoRepository<UserProjects, UUID>