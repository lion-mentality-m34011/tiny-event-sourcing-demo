//package ru.quipy.controller
//
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.PathVariable
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RequestParam
//import org.springframework.web.bind.annotation.RestController
//import ru.quipy.api.*
//import ru.quipy.core.EventSourcingService
//import ru.quipy.logic.*
//import java.util.*
//
//@RestController
//@RequestMapping("/taskAndStatus")
//class TaskAndStatusController(
//    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
//    val userEsService: EventSourcingService<String, UserAggregate, UserAggregateState>,
//    val taskEsService: EventSourcingService<UUID, TaskAndStatusAggregate, TaskAndStatusAggregateState>
//) {
//
//    @PostMapping("/{taskName}")
//    fun createProject(
//        @PathVariable taskName: String,
//        @RequestParam projectId: UUID,
//        @RequestParam statusId: UUID,
//    ) : TaskHasBeenCreatedEvent {
//        projectEsService.getState(projectId)?: throw IllegalArgumentException("Project does not exists.")
//
//        return taskEsService.update(projectId) {
//            it.createTask(UUID.randomUUID(), taskName, projectId, statusId)
//        }
//    }
//
//    @GetMapping("/{taskAndStatusId}")
//    fun getState(@PathVariable taskAndStatusId: UUID) : TaskAndStatusAggregateState? {
//        return taskEsService.getState(taskAndStatusId)
//    }
//}
//
//
