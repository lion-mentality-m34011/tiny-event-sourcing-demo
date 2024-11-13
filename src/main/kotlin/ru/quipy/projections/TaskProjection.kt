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
class TaskProjection (
    private val taskRepository: TaskRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    private val logger = LoggerFactory.getLogger(TaskProjection::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskAndStatusAggregate::class, "tasks::task-projection") {
            `when`(TaskHasBeenCreatedEvent::class) { event ->
                taskRepository.save(Task(event.taskId, event.taskName, mutableListOf(), event.statusId))
                logger.info("Task created ${event.projectId}")
            }
            `when`(UserHasBeenAssignedAsAssigneeEvent::class) { event ->
                var task = taskRepository.findByIdOrNull(event.taskId) ?: throw NotFoundException("Task does not exists")
                task.assignee.add(event.userId)
                taskRepository.save(task)
                logger.info("Add user: ${event.userId} to task: ${event.taskId}")
            }
            `when`(TaskStatusHasBeenChangedEvent::class) { event ->
                var task = taskRepository.findByIdOrNull(event.taskId) ?: throw NotFoundException("Task does not exists")
                task.statusId = event.statusId
                taskRepository.save(task)
                logger.info("Change Task status")
            }
            `when`(TaskHasBeenRenamedEvent::class) { event ->
                var task = taskRepository.findByIdOrNull(event.taskId) ?: throw NotFoundException("Task does not exists")
                task.name = event.name
                taskRepository.save(task)
                logger.info("Change Task name")
            }
        }
    }

    fun getById(taskId: UUID) : Task? {
        return taskRepository.findByIdOrNull(taskId)
    }
}


@Document("task-projection")
data class Task(
    @Id
    var taskId: UUID,
    var name: String,
    val assignee: MutableList<UUID> = mutableListOf(),
    var statusId: UUID,
)

@Repository
interface TaskRepository : MongoRepository<Task, UUID>