package ru.quipy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.api.UserHasBeenCreatedEvent
import ru.quipy.controller.ProjectController
import java.util.*
import ru.quipy.controller.UserController

@SpringBootTest
class ProjectControllerTest {

    @Autowired
    private lateinit var userCtrl: UserController

    @Autowired
    private lateinit var projectCtrl: ProjectController

    @Test
    fun should_create_project_and_add_participant_successfully() {
        val owner = createNewUser()
        val newUser = createNewUser()

        val createdProject = projectCtrl.createProject("New Project", owner.userId)

        assertEquals(1, createdProject.version)
        assertEquals("New Project", createdProject.projectName)

        val retrievedProject = projectCtrl.getProject(createdProject.projectId)
        assertNotNull(retrievedProject)

        val projectOwner = retrievedProject!!.participants.firstOrNull { it == owner.userId }
        assertNotNull(projectOwner)
        assertEquals("New Project", retrievedProject.projectName)
        assertEquals(owner.userId, projectOwner)
        assertEquals(1, retrievedProject.participants.size)

        assertEquals(null, retrievedProject.participants.firstOrNull { it == newUser.userId })

        projectCtrl.addParticipant(retrievedProject.getId(), newUser.userId)

        val updatedProject = projectCtrl.getProject(retrievedProject.getId())
        val addedUser = updatedProject!!.participants.firstOrNull { it == newUser.userId }

        assertNotNull(addedUser)
        assertEquals(newUser.userId, addedUser)
        assertEquals(2, updatedProject.participants.size)
    }

    private fun createNewUser(): UserHasBeenCreatedEvent {
        return userCtrl.createUser(
            UUID.randomUUID().toString(),
            "securePassword"
        )
    }
}