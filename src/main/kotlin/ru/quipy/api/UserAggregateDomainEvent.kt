package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val USER_HAS_BEEN_CREATED_EVENT = "USER_HAS_BEEN_CREATED_EVENT"
const val USER_AGGREGATE_HAS_BEEN_CREATED_EVENT = "USER_AGGREGATE_HAS_BEEN_CREATED_EVENT"

@DomainEvent(name = USER_HAS_BEEN_CREATED_EVENT)
class UserHasBeenCreatedEvent(
    val userId: UUID,
    val login: String,
    val password: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate>(
    name = USER_HAS_BEEN_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = USER_AGGREGATE_HAS_BEEN_CREATED_EVENT)
class UserAggregateHasBeenCreatedEvent(
    createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate>(
    name = USER_AGGREGATE_HAS_BEEN_CREATED_EVENT,
    createdAt = createdAt,
)


