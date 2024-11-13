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
import ru.quipy.projections.ProjectMembers
import ru.quipy.projections.ProjectMembersProjection
import ru.quipy.projections.Task
import ru.quipy.projections.TaskProjection
import java.util.*

@RestController
@RequestMapping("/taskAndStatus")
class TaskAndStatusController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val taskEsService: EventSourcingService<UUID, TaskAndStatusAggregate, TaskAndStatusAggregateState>,
    val taskProjection: TaskProjection,
) {

    @PostMapping("/task/{taskName}")
    fun createTask(
        @PathVariable taskName: String,
        @RequestParam projectId: UUID,
        @RequestParam statusId: UUID,
    ) : TaskHasBeenCreatedEvent {
        val project = projectEsService.getState(projectId)?: throw NotFoundException("Project does not exist.")

        return taskEsService.update(project.taskAndStatusId) {
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

        return taskEsService.update(project.taskAndStatusId) {
            it.renameTask(taskId, newName)
        }
    }

    @GetMapping("/{projectId}")
    fun getState(@PathVariable projectId: UUID) : TaskAndStatusAggregateState? {
        val project = projectEsService.getState(projectId)?: throw NotFoundException("Project does not exist.")
        return taskEsService.getState(project.taskAndStatusId)
    }

    @GetMapping("/projection/task/{taskId}")
    fun getTaskProjection(@PathVariable taskId: UUID) : Task {
        return taskProjection.getById(taskId) ?: throw NotFoundException("Task does not exists")
    }
}


