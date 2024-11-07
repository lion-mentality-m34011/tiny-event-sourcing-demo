package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


fun TaskAndStatusAggregateState.createTask(
    id: UUID,
    name: String,
    projectId: UUID,
    statusId: UUID
): TaskHasBeenCreatedEvent {
    if (name == "") {
        throw IllegalArgumentException("Task name is empty.")
    }
    if (tasks.values.any { t -> t.name == name }) {
        throw IllegalArgumentException("Name already exists.")
    }

    println(statuses)
    if (!statuses.containsKey(statusId)) {
        throw IllegalArgumentException("Status does not exists.")
    }

    return TaskHasBeenCreatedEvent(
        taskId = id,
        taskName = name,
        projectId = projectId,
        statusId = statusId,
    )
}

fun TaskAndStatusAggregateState.addAssignee(
    taskId: UUID,
    userId: UUID
): UserHasBeenAssignedAsAssigneeEvent {
    val task = tasks[taskId] ?: throw IllegalArgumentException("Task not found.")

    if (task.assignees.contains(userId)) {
        throw IllegalArgumentException("Assignee already exists.")
    }

    return UserHasBeenAssignedAsAssigneeEvent(taskId, userId)
}

fun TaskAndStatusAggregateState.renameTask(
    taskId: UUID,
    taskName: String
): TaskHasBeenRenamedEvent {
    val task = tasks[taskId] ?: throw IllegalArgumentException("Task not found.")

    if (taskName == "") {
        throw IllegalArgumentException("Task name is empty.")
    }
    if (tasks.values.any { t -> t.name == taskName }) {
        throw IllegalArgumentException("Name already exists.")
    }

    return TaskHasBeenRenamedEvent(taskId, taskName)
}

fun TaskAndStatusAggregateState.createStatus(
    taskAndStatusId: UUID,
    statusId: UUID,
    statusName: String,
    projectId: UUID,
    statusColor: StatusColor): StatusHasBeenCreatedEvent {
    if (statuses.values.any { t -> t.name == statusName }) {
        throw IllegalArgumentException("Status already exists.")
    }
    println("HOHOHOHOHOHOHOHOOHHOOHOOHOHOHOHOHOH " + projectId + " " + statusId + " " + statusName + " ")
    return StatusHasBeenCreatedEvent(
        projectId = projectId,
        statusId = statusId,
        statusName = statusName,
        statusColour = statusColor,
        taskAndStatusId = taskAndStatusId,
    )
}

fun TaskAndStatusAggregateState.changeStatus(taskId: UUID, statusId: UUID): TaskStatusHasBeenChangedEvent {
    if (!statuses.containsKey(statusId))
        throw IllegalArgumentException("Status does not exist.")

    if (!tasks.containsKey(taskId))
        throw IllegalArgumentException("Task not found.")

    return TaskStatusHasBeenChangedEvent(
        taskId = taskId,
        statusId = statusId,
    )
}

fun TaskAndStatusAggregateState.deleteStatus(statusId: UUID): StatusHasBeenDeletedEvent {
    if (!statuses.containsKey(statusId))
        throw IllegalArgumentException("Status does not exist.")

    if (tasks.values.any { it.statusId == statusId })
        throw IllegalStateException("Status has tasks")

    return StatusHasBeenDeletedEvent(statusId = statusId)
}

fun TaskAndStatusAggregateState.changeStatusOrder(
    statusId: UUID,
    order: Int,
): StatusOrderHasBeenChangedEvent {
    if (!statuses.containsKey(statusId))
        throw IllegalArgumentException("Status does not exist.")


    if (order >= 0 && order < statuses.size)
        return StatusOrderHasBeenChangedEvent(
            statusId = statusId,
            newOrder = order,
        )

    throw IllegalArgumentException("Order is wrong.")


}