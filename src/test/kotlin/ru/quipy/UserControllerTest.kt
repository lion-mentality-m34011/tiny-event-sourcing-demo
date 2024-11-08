package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.quipy.controller.UserController
import java.util.*

@SpringBootTest
class UserControllerTests {

    @Autowired
    private lateinit var userCtrl: UserController

    @Test
    fun getUser() {
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
}