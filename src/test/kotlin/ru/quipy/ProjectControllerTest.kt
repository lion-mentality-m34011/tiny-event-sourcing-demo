package ru.quipy

import org.awaitility.Awaitility
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import ru.quipy.api.ProjectHasBeenCreatedEvent
import ru.quipy.api.TaskHasBeenCreatedEvent
import ru.quipy.api.UserHasBeenCreatedEvent
import ru.quipy.controller.ProjectController
import ru.quipy.controller.TaskAndStatusController
import java.util.*
import ru.quipy.controller.UserController
import ru.quipy.projections.ProjectMembersRepository
import ru.quipy.projections.ProjectRepository
import ru.quipy.projections.ProjectTasksRepository
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit

@SpringBootTest
class ProjectControllerTest {

    @Autowired
    private lateinit var userCtrl: UserController

    @Autowired
    private lateinit var projectCtrl: ProjectController

    @Autowired
    private lateinit var taskAndStatusCtrl: TaskAndStatusController

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    private lateinit var projectMembersRepository: ProjectMembersRepository

    @Autowired
    private lateinit var projectTasksRepository: ProjectTasksRepository

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

        Awaitility.await().timeout(10, TimeUnit.SECONDS).untilAsserted { assertEquals(project.projectId, projectRepository.findByIdOrNull(project.projectId)?.projectId) }

        val projectProjection = projectCtrl.getProjectProjection(project.projectId)
        assertNotNull(projectProjection)
        assertEquals(project.projectId, projectProjection.projectId)
        assertEquals("New Project", projectProjection.name)
    }

    @Test
    fun getProjectMembersProjection() {
        val user = createNewUser()
        val project = projectCtrl.createProject("New Project", user.userId)

        Awaitility.await().timeout(10, TimeUnit.SECONDS).untilAsserted {
            val projectMembers = projectMembersRepository.findByIdOrNull(project.projectId)
            assertTrue(projectMembers?.members?.contains(user.userId) ?: false)
        }

        val projectProjection = projectCtrl.getProjectMembersProjection(project.projectId)
        assertNotNull(projectProjection)
        assertEquals(project.projectId, projectProjection.projectId)
        assertTrue(projectProjection.members.contains(user.userId))
    }

    @Test
    fun getProjectTasksProjection() {
        val user = createNewUser()
        val project = createNewProject(user.userId)

        Awaitility.await().timeout(10, TimeUnit.SECONDS).untilAsserted { assertEquals(project.projectId, projectTasksRepository.findByIdOrNull(project.projectId)?.projectId) }

        val aggregateObj = taskAndStatusCtrl.getState(project.projectId)
        val statusId = aggregateObj!!.statuses.values.first().id
        val task = createNewTask(project.projectId, statusId)

        Awaitility.await().timeout(1, TimeUnit.MINUTES).untilAsserted {
            val projectTasks = projectTasksRepository.findByIdOrNull(project.projectId)
            assertTrue(projectTasks?.tasks?.contains(task.taskId) ?: false)
        }

        val projectProjection = projectCtrl.getProjectTasksProjection(project.projectId)
        assertNotNull(projectProjection)
        assertEquals(project.projectId, projectProjection.projectId)
        assertTrue(projectProjection.tasks.contains(task.taskId))
    }

    private fun createNewUser(): UserHasBeenCreatedEvent {
        return userCtrl.createUser(
            UUID.randomUUID().toString(),
            "securePassword"
        )
    }

    private fun createNewProject(userId: UUID): ProjectHasBeenCreatedEvent {
        return projectCtrl.createProject(UUID.randomUUID().toString(), userId)
    }

    private fun createNewTask(projectId: UUID, statusId: UUID): TaskHasBeenCreatedEvent {
        return taskAndStatusCtrl.createTask(UUID.randomUUID().toString(), projectId, statusId)
    }
}