package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<String, UserAggregate> {
    private lateinit var userId: String

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    var users = mutableMapOf<UUID, UserEntity>()

    private lateinit var aggregateId: String

    override fun getId() = aggregateId

    @StateTransitionFunc
    fun userHasBeenCreatedApply(event: UserHasBeenCreatedEvent) {
        users[event.userId] = UserEntity(
            event.userId,
            event.login,
            event.password
        )
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun userAggregateHasBeenCreatedApply(event: UserAggregateHasBeenCreatedEvent) {
        aggregateId = "user-aggregate-id"
        updatedAt = event.createdAt
    }
}

data class UserEntity(
    var id: UUID,
    var login: String,
    var password: String
)