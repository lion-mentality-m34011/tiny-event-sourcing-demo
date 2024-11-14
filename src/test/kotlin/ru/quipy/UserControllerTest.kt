package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.controller.ProjectController
import ru.quipy.controller.UserController
import ru.quipy.projections.UserProjection
import ru.quipy.streams.AggregateSubscriptionsManager
import java.lang.IllegalArgumentException
import java.util.*

@SpringBootTest
class UserControllerTests {

    @Autowired
    private lateinit var userCtrl: UserController

    @Autowired
    private lateinit var projectCtrl: ProjectController

    @Autowired
    private lateinit var userProjection : UserProjection

    @Test
    fun createUser() {
        val login = UUID.randomUUID().toString()
        val user = userCtrl.createUser(
            login,
            "password"
        )
        Assertions.assertEquals(login, user.login)
        Assertions.assertEquals("password", user.password)

        val response = userCtrl.getAccount(user.userId)
        Assertions.assertNotNull(response)
        Assertions.assertEquals(login, response!!.login)
        Assertions.assertEquals("password", response.password)
    }

    @Test
    fun failCreateUser() {
        val login = UUID.randomUUID().toString()
        userCtrl.createUser(
            login,
            "password"
        )

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            userCtrl.createUser(
                login,
                "password"
            )
        }
    }

    @Test
    fun getUserProjectsProjection() {
        val login = UUID.randomUUID().toString()
        val user = userCtrl.createUser(
            login,
            "password"
        )

        val project = projectCtrl.createProject("New Project", user.userId)

        Thread.sleep(5_000)

        val userProjects = userCtrl.getUserProjectsProjection(user.userId)
        Assertions.assertNotNull(userProjects)
        Assertions.assertEquals(user.userId, userProjects.userId)
        Assertions.assertTrue(userProjects.projects.contains(project.projectId))
    }

    @Test
    fun getUserID() {
        val login = UUID.randomUUID().toString()
        val user = userCtrl.createUser(
            login,
            "password"
        )

        Thread.sleep(5_000)

        val userID = userCtrl.getUserID(login, "password")
        Assertions.assertNotNull(user)
        Assertions.assertNotNull(userID)
        Assertions.assertEquals(user.userId, userID)
    }
}