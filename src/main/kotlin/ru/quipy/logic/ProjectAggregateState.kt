package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectName: String

    var participants = mutableListOf<UUID>()

    override fun getId() = projectId

    @StateTransitionFunc
    fun projectHasBeenCreatedApply(event: ProjectHasBeenCreatedEvent) {
        projectId = event.projectId
        projectName = event.projectName
        participants = event.participants
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun userHasBeenAddedEventApply(event: UserHasBeenAddedEvent) {
        participants.add(event.userId)
        updatedAt = event.createdAt
    }

    fun setEmptyParticipants(): MutableList<UUID> {
        return mutableListOf<UUID>()
    }

}