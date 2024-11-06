package ru.quipy.logic

import javassist.NotFoundException
import ru.quipy.api.TaskAndStatusAggregate
import ru.quipy.api.TaskHasBeenCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*


class TaskAndStatusAggregateState : AggregateState<UUID, TaskAndStatusAggregate> {
    private lateinit var taskId: UUID
    var statuses = mutableMapOf<UUID, StatusEntity>()
    var tasks = mutableMapOf<UUID, TaskEntity>()

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = taskId

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskHasBeenCreatedEvent) {
        tasks[event.taskId] = TaskEntity(
            id = event.taskId,
            projectId = event.projectId,
            name = event.taskName,
            assignees = mutableListOf(),
            statusId = event.statusId,
        )
        updatedAt = createdAt
    }

}


data class StatusColor(
    val red: Int,
    val green: Int,
    val blue: Int
)


data class StatusEntity(
    val id: UUID,
    val projectId: UUID,
    val name: String,
    val color: StatusColor,
)


data class TaskEntity(
    val id: UUID,
    var name: String,
    val projectId: UUID,
    val assignees: MutableList<UUID>,
    var statusId: UUID,
)

