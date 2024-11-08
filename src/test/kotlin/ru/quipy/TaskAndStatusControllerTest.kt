package ru.quipy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.api.ProjectHasBeenCreatedEvent
import ru.quipy.api.UserHasBeenCreatedEvent
import ru.quipy.controller.ProjectController
import ru.quipy.controller.TaskAndStatusController
import ru.quipy.controller.UserController
import ru.quipy.logic.StatusColor
import java.lang.IllegalStateException
import java.util.UUID

@SpringBootTest
class TaskAndStatusControllerTests {

    @Autowired
    private lateinit var userCtrl: UserController

    @Autowired
    private lateinit var projectCtrl: ProjectController

    @Autowired
    private lateinit var taskAndStatusCtrl: TaskAndStatusController

    @Test
    fun createTask() {
        val user = createNewUser()
        val project = createNewProject(user.userId)
        val aggregateObj = taskAndStatusCtrl.getState(project.projectId)
        val statusId = aggregateObj!!.statuses.values.first().id


        val task = taskAndStatusCtrl.createTask("test", project.projectId, statusId)
        assertNotNull(task)
        assertEquals(project.projectId, task.projectId)
        assertEquals("test", task.taskName)
        assertEquals(statusId, task.statusId)
    }

    @Test
    fun createStatus() {
        val user = createNewUser()
        val project = createNewProject(user.userId)
        val aggregateObj = taskAndStatusCtrl.getState(project.projectId)


        val status = taskAndStatusCtrl.createStatus("test", project.projectId, 1, 1, 1)
        assertNotNull(status)
        assertEquals(project.projectId, status.projectId)
        assertEquals(aggregateObj!!.getId(), status.taskAndStatusId)
        assertEquals("test", status.statusName)
        assertEquals(StatusColor(1, 1,1), status.statusColour)
    }

    @Test
    fun deleteStatus() {
        val user = createNewUser()
        val project = createNewProject(user.userId)
        var aggregateObj = taskAndStatusCtrl.getState(project.projectId)
        val statusId = aggregateObj!!.statuses.values.first().id


        taskAndStatusCtrl.createStatus("test", project.projectId, 1, 1, 1)
        val deletedStatus = taskAndStatusCtrl.deleteStatus(project.projectId,statusId)
        assertNotNull(deletedStatus)
        assertEquals(statusId, deletedStatus.statusId)

        aggregateObj = taskAndStatusCtrl.getState(project.projectId)
        val lastStatus = aggregateObj!!.statuses.values.first()
        assertNotNull(lastStatus)
        assertEquals(project.projectId, lastStatus.projectId)
        assertEquals(1, lastStatus.order)
        assertEquals("test", lastStatus.name)
        assertEquals(StatusColor(1, 1,1), lastStatus.statusColor)
    }

    @Test
    fun failDeleteStatus() {
        val user = createNewUser()
        val project = createNewProject(user.userId)
        val aggregateObj = taskAndStatusCtrl.getState(project.projectId)
        val statusId = aggregateObj!!.statuses.values.first().id


        taskAndStatusCtrl.createTask("test", project.projectId, statusId)
        try {
            taskAndStatusCtrl.deleteStatus(project.projectId,statusId)
        } catch (_: IllegalStateException){
        }
    }



    @Test
    fun changeOrderStatus() {
        val user = createNewUser()
        val project = createNewProject(user.userId)
        var aggregateObj = taskAndStatusCtrl.getState(project.projectId)
        val statusId = aggregateObj!!.statuses.values.first().id


        val newStatus = taskAndStatusCtrl.createStatus("test", project.projectId, 1, 1, 1)
        val changedStatus = taskAndStatusCtrl.changeOrderStatus(project.projectId,statusId,2)
        assertNotNull(changedStatus)
        assertEquals(statusId, changedStatus.statusId)

        aggregateObj = taskAndStatusCtrl.getState(project.projectId)
        val status1 = aggregateObj!!.statuses[newStatus.statusId]
        val status2 = aggregateObj.statuses[statusId]

        assertNotNull(status1)
        assertEquals(project.projectId, status1!!.projectId)
        assertEquals(1, status1.order)
        assertEquals("test", status1.name)
        assertEquals(StatusColor(1, 1,1), status1.statusColor)

        assertNotNull(status2)
        assertEquals(project.projectId, status2!!.projectId)
        assertEquals(2, status2.order)
        assertEquals("CREATED", status2.name)
        assertEquals(StatusColor(0, 0,0), status2.statusColor)
    }

    @Test
    fun addAssignee() {
        val user = createNewUser()
        val project = createNewProject(user.userId)
        val aggregateObj = taskAndStatusCtrl.getState(project.projectId)
        val statusId = aggregateObj!!.statuses.values.first().id


        val task = taskAndStatusCtrl.createTask("test", project.projectId, statusId)
        val assignee = taskAndStatusCtrl.addAssignee(project.projectId,user.userId, task.taskId)

        assertNotNull(assignee)
        assertEquals(task.taskId, assignee.taskId)
        assertEquals(user.userId, assignee.userId)
    }

    @Test
    fun changeStatus() {
        val user = createNewUser()
        val project = createNewProject(user.userId)
        val aggregateObj = taskAndStatusCtrl.getState(project.projectId)
        val statusId = aggregateObj!!.statuses.values.first().id
        val newStatus = taskAndStatusCtrl.createStatus("test", project.projectId, 1, 1, 1)


        val task = taskAndStatusCtrl.createTask("test", project.projectId, statusId)
        val statusChange = taskAndStatusCtrl.changeStatus(project.projectId, task.taskId, newStatus.statusId)

        assertNotNull(statusChange)
        assertEquals(task.taskId, statusChange.taskId)
        assertEquals(newStatus.statusId, statusChange.statusId)
    }

    @Test
    fun renameTask() {
        val user = createNewUser()
        val project = createNewProject(user.userId)
        val aggregateObj = taskAndStatusCtrl.getState(project.projectId)
        val statusId = aggregateObj!!.statuses.values.first().id


        val task = taskAndStatusCtrl.createTask("test", project.projectId, statusId)
        val renamedTask = taskAndStatusCtrl.renameTask(project.projectId, task.taskId, "test1")

        assertNotNull(renamedTask)
        assertEquals(task.taskId, renamedTask.taskId)
        assertEquals("test1", renamedTask.taskName)
    }

    private fun createNewUser(): UserHasBeenCreatedEvent {
        return userCtrl.createUser(
            UUID.randomUUID().toString(),
            "securePassword"
        )
    }

    private fun createNewProject(userId: UUID): ProjectHasBeenCreatedEvent {
        return projectCtrl.createProject(
            UUID.randomUUID().toString(),
            userId,
        )
    }
}