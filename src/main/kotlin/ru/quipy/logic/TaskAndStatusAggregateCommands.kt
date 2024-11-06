package ru.quipy.logic

import ru.quipy.api.TaskHasBeenCreatedEvent
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

    return TaskHasBeenCreatedEvent(
        taskId = id,
        taskName = name,
        projectId = projectId,
        statusId = statusId,
    )
}