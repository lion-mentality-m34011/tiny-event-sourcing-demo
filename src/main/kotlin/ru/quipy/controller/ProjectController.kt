package ru.quipy.controller

import javassist.NotFoundException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectHasBeenCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserHasBeenAddedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.addUser
import ru.quipy.logic.createProject
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val userEsService: EventSourcingService<String, UserAggregate, UserAggregateState>
) {

    @PostMapping("/{projectName}")
    fun createProject(@PathVariable projectName: String, @RequestParam creatorId: UUID) : ProjectHasBeenCreatedEvent {
        userEsService.getState("user-aggregate-id")?.users?.get(creatorId) ?: throw NotFoundException("User has not been added")
        val project = projectEsService.create { it.createProject(UUID.randomUUID(), projectName) }

//        TODO:
//        taskEsService.create {
//            it.createStatus(UUID.randomUUID(), "CREATED", response.projectId, ColorEnum.GREEN)
//        }

        projectEsService.update(project.projectId) {
            it.addUser(id = project.projectId, userId = creatorId)
        }

        return project
    }

    @GetMapping("/{projectId}")
    fun getAccount(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/participants/add")
    fun addParticipant(
        @PathVariable projectId: UUID,
        @RequestParam userId: UUID
    ): UserHasBeenAddedEvent {
       userEsService.getState("user-aggregate-id")?.users?.get(userId) ?: throw NotFoundException("User has not been added")
        return projectEsService.update(projectId) { it.addUser(id = projectId, userId = userId) }
    }
}


