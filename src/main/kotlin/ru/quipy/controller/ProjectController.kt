package ru.quipy.controller

import javassist.NotFoundException
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
@RequestMapping("/project")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val userEsService: EventSourcingService<String, UserAggregate, UserAggregateState>,
    val taskEsService: EventSourcingService<UUID, TaskAndStatusAggregate, TaskAndStatusAggregateState>
) {

    @PostMapping("/{projectName}")
    fun createProject(@PathVariable projectName: String, @RequestParam creatorId: UUID) : ProjectHasBeenCreatedEvent {
        userEsService.getState("user-aggregate-id")?.users?.get(creatorId) ?: throw NotFoundException("User has not been added")
        val project = projectEsService.create { it.createProject(UUID.randomUUID(), projectName) }

        val aggregateId = UUID.randomUUID()
        taskEsService.create {
            it.createStatus(aggregateId, UUID.randomUUID(), "CREATED", project.projectId, StatusColor(0, 0,0))
        }

        projectEsService.update(project.projectId) {
            it.addUser(id = project.projectId, userId = creatorId)
        }

        projectEsService.update(project.projectId) {
            it.addTaskAndStatusAggregateId(id = project.projectId, taskAndStatusId = aggregateId)
        }


        return project
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/participants/add")
    fun addParticipant(
        @PathVariable projectId: UUID,
        @RequestParam userId: UUID
    ): UserHasBeenAddedEvent {
       userEsService.getState("user-aggregate-id")?.users?.get(userId) ?: throw NotFoundException("User does not exists")
        return projectEsService.update(projectId) { it.addUser(id = projectId, userId = userId) }
    }
}


