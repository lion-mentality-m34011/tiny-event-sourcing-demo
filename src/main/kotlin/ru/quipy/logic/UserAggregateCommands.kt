package ru.quipy.logic

import ru.quipy.api.UserAggregateHasBeenCreatedEvent
import ru.quipy.api.UserHasBeenCreatedEvent
import java.util.*


fun UserAggregateState.createUser(
    userId: UUID,
    login: String,
    password: String
): UserHasBeenCreatedEvent {
    if (users.values.any { x -> x.login == login }) {
        throw IllegalArgumentException("User has already been created.")
    }
    return UserHasBeenCreatedEvent(
        userId = userId,
        login = login,
        password = password,
    )
}

fun UserAggregateState.create(id: String): UserAggregateHasBeenCreatedEvent {
    return UserAggregateHasBeenCreatedEvent()
}

