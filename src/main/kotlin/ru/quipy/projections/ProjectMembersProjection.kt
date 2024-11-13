package ru.quipy.projections

import javassist.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectHasBeenCreatedEvent
import ru.quipy.api.UserHasBeenAddedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct


@Component
class ProjectMembersProjection (
    private val projectMembersRepository: ProjectMembersRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    private val logger = LoggerFactory.getLogger(ProjectMembersProjection::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "projects::project-members-projection") {
            `when`(ProjectHasBeenCreatedEvent::class) { event ->
                projectMembersRepository.save(ProjectMembers(event.projectId, mutableListOf()))
                logger.info("Create members project ${event.projectId}")
            }
            `when`(UserHasBeenAddedEvent::class) { event ->
                var projectMembers = projectMembersRepository.findByIdOrNull(event.projectId) ?: throw NotFoundException("Project does not exists")
                projectMembers.members.add(event.userId)
                projectMembersRepository.save(projectMembers)
                logger.info("Add user: ${event.userId} to project: ${event.projectId}")
            }
        }
    }

    fun getById(projectId: UUID) : ProjectMembers? {
        return projectMembersRepository.findByIdOrNull(projectId)
    }
}


@Document("project-members-projection")
data class ProjectMembers(
    @Id
    var projectId: UUID,
    val members: MutableList<UUID> = mutableListOf()
)

@Repository
interface ProjectMembersRepository : MongoRepository<ProjectMembers, UUID>