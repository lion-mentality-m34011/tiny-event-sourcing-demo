package ru.quipy.projections

import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectHasBeenCreatedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct


@Component
class ProjectProjection (
    private val projectRepository: ProjectRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    private val logger = LoggerFactory.getLogger(ProjectProjection::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "projects::project-projection") {
            `when`(ProjectHasBeenCreatedEvent::class) { event ->
                projectRepository.save(Project(event.projectId, event.projectName))
                logger.info("Create project ${event.projectId}")
            }
        }
    }

    fun getById(projectId: UUID) : Project? {
        return projectRepository.findByIdOrNull(projectId)
    }
}


@Document("project-projection")
data class Project(
    @Id
    var projectId: UUID,
    val name: String,
)

@Repository
interface ProjectRepository : MongoRepository<Project, UUID>