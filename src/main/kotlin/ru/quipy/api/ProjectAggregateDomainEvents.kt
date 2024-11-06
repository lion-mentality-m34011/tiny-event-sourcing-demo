package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_HAS_BEEN_CREATED = "PROJECT_HAS_BEEN_CREATED"
const val USER_HAS_BEEN_ADDED_EVENT = "USER_HAS_BEEN_ADDED_EVENT"


@DomainEvent(name = PROJECT_HAS_BEEN_CREATED)
class ProjectHasBeenCreatedEvent(
    val projectId: UUID,
    val projectName: String,
    val participants: MutableList<UUID>,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_HAS_BEEN_CREATED,
    createdAt = createdAt,
)


@DomainEvent(name = USER_HAS_BEEN_ADDED_EVENT)
class UserHasBeenAddedEvent(
    val projectId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = USER_HAS_BEEN_ADDED_EVENT, createdAt = createdAt
)

