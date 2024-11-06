package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


fun ProjectAggregateState.createProject(id: UUID, name: String): ProjectHasBeenCreatedEvent {
    return ProjectHasBeenCreatedEvent(
        projectId = id,
        projectName = name,
        participants = setEmptyParticipants(),
    )
}

fun ProjectAggregateState.addUser(id: UUID, userId: UUID): UserHasBeenAddedEvent {
    if (participants.contains(userId))
        throw IllegalArgumentException("User has already been added.")
    return UserHasBeenAddedEvent(
        projectId = id,
        userId = userId,
    )
}
