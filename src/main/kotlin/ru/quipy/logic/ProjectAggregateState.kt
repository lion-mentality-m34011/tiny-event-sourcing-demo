package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*
import kotlin.collections.mutableMapOf

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectName: String

    var statuses = mutableListOf<TaskStatusEntity>()
    var participants = mutableListOf<UUID>()

    override fun getId() = projectId

    @StateTransitionFunc
    fun projectHasBeenCreatedApply(event: ProjectHasBeenCreatedEvent) {
        projectId = event.projectId
        projectName = event.projectName
        statuses = event.statuses
        participants = event.participants
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun userHasBeenAddedEventApply(event: UserHasBeenAddedEvent) {
        participants.add(event.userId)
        updatedAt = event.createdAt
    }

    fun withDefaultStatus() : MutableList<TaskStatusEntity> {
        return mutableListOf<TaskStatusEntity>(
            TaskStatusEntity(
                id = UUID.randomUUID(), name = "Created", colour = StatusColor(0, 0, 0)
            )
        )
    }

    fun setEmptyParticipants() : MutableList<UUID> {
        return mutableListOf<UUID>()
    }

}

data class TaskStatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val colour: StatusColor,
)