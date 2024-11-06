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

        val id_ = UUID.randomUUID()
        val a = taskEsService.create {
            it.createStatus(id_, "CREATED", project.projectId, StatusColor(0, 0,0))
//            it.createTask(UUID.randomUUID(), "1111", project.projectId, id_)
        }
        val b = taskEsService.create {
            it.createStatus(id_, "CREATED", project.projectId, StatusColor(0, 0,0))
        }

        println(a.projectId)
        println(taskEsService.getState(a.projectId))
        println(taskEsService.getState(a.id))
        println(taskEsService.getState(a.statusId))


//        taskAndStatusEsService.update(project.projectId) {
//            it.createStatus(UUID.randomUUID(), "CREATED", project.projectId, StatusColor(0, 0,0))
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
       userEsService.getState("user-aggregate-id")?.users?.get(userId) ?: throw NotFoundException("User does not exists")
        return projectEsService.update(projectId) { it.addUser(id = projectId, userId = userId) }
    }
}


