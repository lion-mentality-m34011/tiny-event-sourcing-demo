package ru.quipy.controller

import javassist.NotFoundException
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/taskAndStatus")
class TaskAndStatusController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val taskEsService: EventSourcingService<UUID, TaskAndStatusAggregate, TaskAndStatusAggregateState>
) {

    @PostMapping("/task/{taskName}")
    fun createTask(
        @PathVariable taskName: String,
        @RequestParam projectId: UUID,
        @RequestParam statusId: UUID,
    ) : TaskHasBeenCreatedEvent {
        projectEsService.getState(projectId)?: throw NotFoundException("Project does not exist.")

        return taskEsService.update(projectId) {
            it.createTask(UUID.randomUUID(), taskName, projectId, statusId)
        }
    }

    @PostMapping("/status/{statusName}")
    fun createStatus(
        @PathVariable statusName: String,
        @RequestParam projectId: UUID,
        @RequestParam red: Int,
        @RequestParam green: Int,
        @RequestParam blue: Int,
    ) : StatusHasBeenCreatedEvent {
        val project = projectEsService.getState(projectId)?: throw NotFoundException("Project does not exist.")

        return taskEsService.update(project.taskAndStatusId) {
            it.createStatus(project.taskAndStatusId, UUID.randomUUID(), statusName, project.getId(), StatusColor(red, green,blue))
        }
    }

    @DeleteMapping("/status")
    fun deleteStatus(
        @RequestParam projectId: UUID,
        @RequestParam statusId: UUID,
    ) : StatusHasBeenDeletedEvent {
        val project = projectEsService.getState(projectId)?: throw NotFoundException("Project does not exist.")
        val taskAndStatus = taskEsService.getState(project.taskAndStatusId)

        if (!taskAndStatus!!.statuses.containsKey(statusId))
            throw NotFoundException("Status does not exist.")

        return taskEsService.update(project.taskAndStatusId) {
            it.deleteStatus(statusId)
        }
    }

    @PostMapping("/status/change_order")
    fun changeOrderStatus(
        @RequestParam projectId: UUID,
        @RequestParam statusId: UUID,
        @RequestParam newOrder: Int,
    ) : StatusOrderHasBeenChangedEvent {
        val project = projectEsService.getState(projectId)?: throw NotFoundException("Project does not exist.")
        val taskAndStatus = taskEsService.getState(project.taskAndStatusId)

        if (!taskAndStatus!!.statuses.containsKey(statusId))
            throw NotFoundException("Status does not exist.")

        return taskEsService.update(project.taskAndStatusId) {
            it.changeStatusOrder(statusId,newOrder)
        }
    }

    @PostMapping("/task/add_assignee")
    fun addAssignee(
        @RequestParam projectId: UUID,
        @RequestParam userId: UUID,
        @RequestParam taskId: UUID,
    ) : UserHasBeenAssignedAsAssigneeEvent {
        val project = projectEsService.getState(projectId)?: throw NotFoundException("Project does not exist.")
        val taskAndStatus = taskEsService.getState(project.taskAndStatusId)

        if (project.participants.firstOrNull { it == userId } == null)
            throw NotFoundException("Project assignee does not exist.")

        if (!taskAndStatus!!.tasks.containsKey(taskId))
            throw NotFoundException("Task does not exist.")

        return taskEsService.update(project.taskAndStatusId) {
            it.addAssignee(taskId, userId)
        }
    }

    @PostMapping("/task/change_status")
    fun changeStatus(
        @RequestParam projectId: UUID,
        @RequestParam taskId: UUID,
        @RequestParam statusId: UUID,
    ) : TaskStatusHasBeenChangedEvent {
        val project = projectEsService.getState(projectId)?: throw NotFoundException("Project does not exist.")
        val taskAndStatus = taskEsService.getState(project.taskAndStatusId)

        if (!taskAndStatus!!.statuses.containsKey(statusId))
            throw NotFoundException("Status does not exist.")

        if (!taskAndStatus.tasks.containsKey(taskId))
            throw NotFoundException("Task does not exist.")

        return taskEsService.update(project.taskAndStatusId) {
            it.changeStatus(taskId, statusId)
        }
    }

    @PostMapping("/task/rename")
    fun renameTask(
        @RequestParam projectId: UUID,
        @RequestParam taskId: UUID,
        @RequestParam newName: String,
    ) : TaskHasBeenRenamedEvent {
        val project = projectEsService.getState(projectId)?: throw NotFoundException("Project does not exist.")
        val taskAndStatus = taskEsService.getState(project.taskAndStatusId)

        if (!taskAndStatus!!.tasks.containsKey(taskId))
            throw NotFoundException("Task does not exist.")

        return taskEsService.update(project.taskAndStatusId) {
            it.renameTask(taskId, newName)
        }
    }


    @GetMapping("/{projectId}")
    fun getState(@PathVariable projectId: UUID) : TaskAndStatusAggregateState? {
        val project = projectEsService.getState(projectId)?: throw NotFoundException("Project does not exist.")
        return taskEsService.getState(project.taskAndStatusId)
    }
}


