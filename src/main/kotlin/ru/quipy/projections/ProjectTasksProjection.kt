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
class ProjectTasksProjection (
    private val projectTasksRepository: ProjectTasksRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    private val logger = LoggerFactory.getLogger(ProjectTasksProjection::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project::project-tasks-projection") {
            `when`(ProjectHasBeenCreatedEvent::class) { event ->
                projectTasksRepository.save(ProjectTasks(event.projectId, mutableListOf()))
                logger.info("Create project-tasks-projection ${event.projectId}")
            }
        }
        subscriptionsManager.createSubscriber(TaskAndStatusAggregate::class, "task-and-status::project-tasks-projection") {
            `when`(TaskHasBeenCreatedEvent::class) { event ->
                var project = projectTasksRepository.findByIdOrNull(event.projectId) ?: throw NotFoundException("Project does not exists")
                project.tasks.add(event.taskId)
                projectTasksRepository.save(project)
                logger.info("task-and-status::project-tasks-projection ${event.projectId}")
            }
        }
    }

    fun getById(projectId: UUID) : ProjectTasks? {
        return projectTasksRepository.findByIdOrNull(projectId)
    }
}


@Document("project-tasks-projection")
data class ProjectTasks(
    @Id
    var projectId: UUID,
    val tasks: MutableList<UUID> = mutableListOf()
)

@Repository
interface ProjectTasksRepository : MongoRepository<ProjectTasks, UUID>