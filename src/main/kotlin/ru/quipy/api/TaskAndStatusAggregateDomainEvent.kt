package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.logic.StatusColor

import java.util.*


const val TASK_HAS_BEEN_CREATED_EVENT = "TASK_HAS_BEEN_CREATED_EVENT"
const val STATUS_HAS_BEEN_CREATED_EVENT = "STATUS_HAS_BEEN_CREATED_EVENT"
const val STATUS_HAS_BEEN_DELETED_EVENT = "STATUS_HAS_BEEN_DELETED_EVENT"
const val STATUS_ORDER_HAS_BEEN_CHANGED_EVENT = "STATUS_ORDER_HAS_BEEN_CHANGED_EVENT"
const val USER_HAS_BEEN_ASSIGNED_AS_ASSIGNEE_EVENT = "USER_HAS_BEEN_ASSIGNED_AS_ASSIGNEE_EVENT"
const val TASK_STATUS_HAS_BEEN_CHANGED_EVENT = "TASK_STATUS_HAS_BEEN_CHANGED_EVENT"
const val TASK_HAS_BEEN_RENAMED_EVENT = "TASK_HAS_BEEN_RENAMED_EVENT"


@DomainEvent(name = TASK_HAS_BEEN_CREATED_EVENT)
class TaskHasBeenCreatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskName: String,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAndStatusAggregate>(
    name = TASK_HAS_BEEN_CREATED_EVENT,
    createdAt = createdAt
)

//@DomainEvent(name = STATUS_HAS_BEEN_CREATED_EVENT)
//class StatusHasBeenCreatedEvent(
//    val projectId: UUID,
//    val statusId: UUID,
//    val statusName: String,
//    val statusColour: StatusColor,
//    createdAt: Long = System.currentTimeMillis(),
//) : Event<TaskAndStatusAggregate>(
//    name = STATUS_HAS_BEEN_CREATED_EVENT,
//    createdAt = createdAt,
//)
//
//
//@DomainEvent(name = STATUS_HAS_BEEN_DELETED_EVENT)
//class StatusHasBeenDeletedEvent(
//    val projectId: UUID,
//    val statusId: UUID,
//    createdAt: Long = System.currentTimeMillis(),
//) : Event<TaskAndStatusAggregate>(
//    name = STATUS_HAS_BEEN_DELETED_EVENT,
//    createdAt = createdAt,
//)
//
//
//@DomainEvent(name = STATUS_ORDER_HAS_BEEN_CHANGED_EVENT)
//class StatusOrderHasBeenChangedEvent(
//    val projectId: UUID,
//    val statusId: UUID,
//    createdAt: Long = System.currentTimeMillis(),
//) : Event<TaskAndStatusAggregate>(
//    name = STATUS_ORDER_HAS_BEEN_CHANGED_EVENT,
//    createdAt = createdAt,
//)
//
//
//@DomainEvent(name = USER_HAS_BEEN_ASSIGNED_AS_ASSIGNEE_EVENT)
//class UserHasBeenAssignedAsAssigneeEvent(
//    val taskId: UUID,
//    val userId: UUID,
//    createdAt: Long = System.currentTimeMillis(),
//) : Event<TaskAndStatusAggregate>(
//    name = USER_HAS_BEEN_ASSIGNED_AS_ASSIGNEE_EVENT,
//    createdAt = createdAt
//)
//
//
//@DomainEvent(name = TASK_STATUS_HAS_BEEN_CHANGED_EVENT)
//class TaskStatusHasBeenChangedEvent(
//    val taskId: UUID,
//    val statusId: UUID,
//    createdAt: Long = System.currentTimeMillis(),
//) : Event<TaskAndStatusAggregate>(
//    name = TASK_STATUS_HAS_BEEN_CHANGED_EVENT,
//    createdAt = createdAt
//)
//
//
//@DomainEvent(name = TASK_HAS_BEEN_RENAMED_EVENT)
//class TaskHasBeenRenamedEvent(
//    val taskId: UUID,
//    val taskName: String,
//    createdAt: Long = System.currentTimeMillis(),
//) : Event<TaskAndStatusAggregate>(
//    name = TASK_HAS_BEEN_RENAMED_EVENT,
//    createdAt = createdAt
//)