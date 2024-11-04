package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


fun ProjectAggregateState.createProject(id: UUID, name: String): ProjectHasBeenCreatedEvent {
    return ProjectHasBeenCreatedEvent(
        projectId = id,
        projectName = name,
        statuses = withDefaultStatus(),
        participants = setEmptyParticipants(),
    )
}

fun ProjectAggregateState.addUser(id: UUID, userId: UUID): UserHasBeenAddedEvent {
    return UserHasBeenAddedEvent(
        projectId = id,
        userId = userId,
    )
}
