package com.anjo.spacetradersservice.controller

import com.anjo.spacetradersservice.TestcontainersConfiguration
import com.anjo.spacetradersservice.model.SpaceTraderUser
import com.anjo.spacetradersservice.repository.SpaceTraderRepository
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthControllerTest {
    @LocalServerPort
    val port: Int = 0
    @Autowired
    private lateinit var spaceTraderRepository: SpaceTraderRepository
    val expectedToken = UUID.randomUUID().toString()

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun dataSourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("app.baseUrl") { "someEndpoint" }
        }
    }

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
        val user = SpaceTraderUser(name = "JohnyGood", email = "johnnyG@example.com", token = expectedToken,
                resetDate = LocalDateTime.now().plusDays(10), isActive = true)
        spaceTraderRepository.save(user)
    }

    @AfterEach
    fun tearDown() {
        spaceTraderRepository.deleteAll()
    }

    @Test
    fun `given a mocked user when ask about token by email then return token`() {
        given()
                .contentType(ContentType.TEXT)
                .queryParam("email", "johnnyG@example.com" )
                .get("/api/auth/email")
                .then()
                .statusCode(200)
                .contentType(ContentType.TEXT)
                .body(containsString(expectedToken))
    }

    @Test
    fun `given a mocked user when ask about token by name then return token`() {
        given()
                .contentType(ContentType.TEXT)
                .queryParam("userName", "JohnyGood" )
                .get("/api/auth/username")
                .then()
                .statusCode(200)
                .contentType(ContentType.TEXT)
                .body(containsString(expectedToken))
    }

    @Test
    fun `given a mocked user when ask about not existing token by name then return 404 not found`() {
        //given

        val expMessage = "Token for user with userName: NonExist not found"

        //when and then
        given()
                .contentType(ContentType.TEXT)
                .queryParam("userName", "NonExist")
                .get("/api/auth/username")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("httpStatusCode", `is`(404))
                .body("errorMessage", `is`(expMessage))
    }


    @Test
    fun `given a mocked user when ask about not existing token by email then return 404 not found`() {
        //given

        val expMessage = "Token for user with email: NonExist@example.com not found"

        //when and then
        given()
                .contentType(ContentType.TEXT)
                .queryParam("email", "NonExist@example.com")
                .get("/api/auth/email")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("httpStatusCode", `is`(404))
                .body("errorMessage", `is`(expMessage))
    }
}