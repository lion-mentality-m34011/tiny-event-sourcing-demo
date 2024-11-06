package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserHasBeenCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.UserEntity
import ru.quipy.logic.create
import ru.quipy.logic.createUser
import java.util.*

@RestController
@RequestMapping("/user")
class UserController(
    val userEsService: EventSourcingService<String, UserAggregate, UserAggregateState>
) {

    @PostMapping("/create")
    fun createUser(
        @RequestParam(required = true, value = "login") login: String,
        @RequestParam(required = true, value = "password") password: String) : UserHasBeenCreatedEvent {
        if (userEsService.getState("user-aggregate-id") == null) {
            userEsService.create { it.create("user-aggregate-id") }
        }
        return userEsService.update("user-aggregate-id") {
            it.createUser(UUID.randomUUID(), login, password)
        }
    }

    @GetMapping("/{userId}")
    fun getAccount(@PathVariable userId: UUID) : UserEntity? {
        return userEsService.getState("user-aggregate-id")?.users?.get(userId)
    }
}