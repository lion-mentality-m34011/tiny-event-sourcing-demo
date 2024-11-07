package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*


class TaskAndStatusAggregateState : AggregateState<UUID, TaskAndStatusAggregate> {
    private lateinit var taskAndStatusId: UUID
    private lateinit var projectId: UUID
    var statuses = mutableMapOf<UUID, StatusEntity>()
    var tasks = mutableMapOf<UUID, TaskEntity>()

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    override fun getId() = taskAndStatusId

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskHasBeenCreatedEvent) {
        tasks[event.taskId] = TaskEntity(
            id = event.taskId,
            projectId = event.projectId,
            name = event.taskName,
            assignees = mutableListOf(),
            statusId = event.statusId,
        )
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun userHasBeenAssignedAsAssigneeEventApply(event: UserHasBeenAssignedAsAssigneeEvent) {
        tasks[event.taskId]!!.assignees.add(event.userId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskHasBeenRenamedEventApply(event: TaskHasBeenRenamedEvent) {
        tasks[event.taskId]!!.name = event.taskName
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusHasBeenCreatedEventApply(event: StatusHasBeenCreatedEvent) {
        if (!this::taskAndStatusId.isInitialized) {
            taskAndStatusId = event.taskAndStatusId
            createdAt = event.createdAt
        }
        projectId = event.projectId
        println("2222222222222222222222222222 " + getId())
        statuses[event.statusId] = StatusEntity(
            id = event.statusId,
            name = event.statusName,
            statusColor = event.statusColour,
            projectId = event.projectId,
            order = statuses.size + 1
        )
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusHasBeenChangedEventApply(event: TaskStatusHasBeenChangedEvent) {
        tasks[event.taskId]?.statusId = event.statusId
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusHasBeenDeletedEvent) {

        val orderDeletedStatus = statuses[event.statusId]!!.order

        reorderStatuses(orderDeletedStatus, false)

        statuses.remove(event.statusId)
        updatedAt = event.createdAt
    }

    private fun reorderStatuses(anchor: Int, isIIncrement: Boolean) {
        statuses.entries.forEach {
            if (if (isIIncrement) (it.value.order > anchor) else (it.value.order <= anchor)) {
                statuses[it.key]!!.order += 1 * (if (isIIncrement) 1 else -1)
            }
        }
    }

    private fun reorderStatusesWithIf(anchor: Int, newAnchor: Int, isIIncrement: Boolean) {
        statuses.entries.forEach {
            if (if (isIIncrement) (it.value.order in newAnchor until anchor) else (it.value.order in (anchor + 1)..newAnchor)) {
                statuses[it.key]!!.order += 1 * (if (isIIncrement) 1 else -1)
            }
        }
    }

    @StateTransitionFunc
    fun statusOrderHasBeenChangedApply(event: StatusOrderHasBeenChangedEvent) {
        val oldOrderStatus = statuses[event.statusId]!!.order

        reorderStatusesWithIf(oldOrderStatus, event.newOrder, event.newOrder < oldOrderStatus)

        statuses[event.statusId]!!.order = event.newOrder

        updatedAt = event.createdAt
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
    val statusColor: StatusColor,
    var order: Int
)


data class TaskEntity(
    val id: UUID,
    var name: String,
    val projectId: UUID,
    val assignees: MutableList<UUID>,
    var statusId: UUID,
)

