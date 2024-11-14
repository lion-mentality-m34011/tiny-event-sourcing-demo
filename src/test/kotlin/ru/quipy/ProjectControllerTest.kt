package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.api.UserHasBeenCreatedEvent
import ru.quipy.controller.ProjectController
import ru.quipy.controller.TaskAndStatusController
import java.util.*
import ru.quipy.controller.UserController
import java.lang.IllegalArgumentException

@SpringBootTest
class ProjectControllerTest {

    @Autowired
    private lateinit var userCtrl: UserController

    @Autowired
    private lateinit var projectCtrl: ProjectController

    @Autowired
    private lateinit var taskAndStatusCtrl: TaskAndStatusController

    @Test
    fun should_create_project_and_add_participant_successfully() {
        val creator = createNewUser()
        val newUser = createNewUser()

        val createdProject = projectCtrl.createProject("New Project", creator.userId)

        assertEquals(1, createdProject.version)
        assertEquals("New Project", createdProject.projectName)

        val retrievedProject = projectCtrl.getProject(createdProject.projectId)
        assertNotNull(retrievedProject)

        val projectOwner = retrievedProject!!.participants.firstOrNull { it == creator.userId }
        assertNotNull(projectOwner)
        assertEquals("New Project", retrievedProject.projectName)
        assertEquals(creator.userId, projectOwner)
        assertEquals(1, retrievedProject.participants.size)

        assertEquals(null, retrievedProject.participants.firstOrNull { it == newUser.userId })

        projectCtrl.addParticipant(retrievedProject.getId(), newUser.userId)

        val updatedProject = projectCtrl.getProject(retrievedProject.getId())
        val addedUser = updatedProject!!.participants.firstOrNull { it == newUser.userId }

        assertNotNull(addedUser)
        assertEquals(newUser.userId, addedUser)
        assertEquals(2, updatedProject.participants.size)
    }

    @Test
    fun addExistedUser() {
        val user = createNewUser()
        val project = projectCtrl.createProject("New Project", user.userId)

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            projectCtrl.addParticipant(project.projectId, user.userId)
        }
    }

    @Test
    fun getProjectProjection() {
        val user = createNewUser()
        val project = projectCtrl.createProject("New Project", user.userId)

        Thread.sleep(5_000)

        val projectProjection = projectCtrl.getProjectProjection(project.projectId)
        assertNotNull(projectProjection)
        assertEquals(project.projectId, projectProjection.projectId)
        assertEquals("New Project", projectProjection.name)
    }

    @Test
    fun getProjectMembersProjection() {
        val user = createNewUser()
        val project = projectCtrl.createProject("New Project", user.userId)

        Thread.sleep(5_000)

        val projectProjection = projectCtrl.getProjectMembersProjection(project.projectId)
        assertNotNull(projectProjection)
        assertEquals(project.projectId, projectProjection.projectId)
        Assertions.assertTrue(projectProjection.members.contains(user.userId))
    }

    @Test
    fun getProjectTasksProjection() {
        val user = createNewUser()
        val project = projectCtrl.createProject("New Project", user.userId)
        val aggregateObj = taskAndStatusCtrl.getState(project.projectId)
        val statusId = aggregateObj!!.statuses.values.first().id
        val task = taskAndStatusCtrl.createTask("test", project.projectId, statusId)

        Thread.sleep(5_000)

        val projectProjection = projectCtrl.getProjectTasksProjection(project.projectId)
        assertNotNull(projectProjection)
        assertEquals(project.projectId, projectProjection.projectId)
        Assertions.assertTrue(projectProjection.tasks.contains(task.taskId))
    }

    private fun createNewUser(): UserHasBeenCreatedEvent {
        return userCtrl.createUser(
            UUID.randomUUID().toString(),
            "securePassword"
        )
    }
}