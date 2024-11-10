package com.anjo.spacetradersservice.controller

import com.anjo.spacetradersservice.TestcontainersConfiguration
import com.anjo.spacetradersservice.generated.infrastructure.RequestMethod.GET
import com.anjo.spacetradersservice.testutils.mockExternalQuery
import com.anjo.spacetradersservice.testutils.mockExternalQueryWithAuth
import com.anjo.spacetradersservice.testutils.verifyMockServerRequest
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockserver.client.MockServerClient
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.UUID
import kotlin.test.Test

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FactionsControllerTest {
    @LocalServerPort
    val port: Int = 0
    private lateinit var mockServerClient: MockServerClient

    companion object {
        @Container
        val mockServerContainer = MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.15.0"))

        @JvmStatic
        @DynamicPropertySource
        fun dataSourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("app.baseUrl") { mockServerContainer.endpoint }
        }
    }

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
        mockServerClient = MockServerClient(mockServerContainer.host, mockServerContainer.serverPort)
    }

    @AfterEach
    fun tearDown() {
        mockServerClient.reset()
    }

    @Test
    fun `given mock response when getAllFactions then return response`() {
        //given
        val path = "/factions"
        val jsnoPath = "/jsonresponses/factionsResponse.json"

        mockExternalQuery(mockServerClient, GET, path, jsnoPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .get("/api/factions")
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("meta", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/factions", 1)
    }

    @Test
    fun `given mock response when getFactions then return response`() {
        //given
        val token = UUID.randomUUID().toString()
        val path = "/factions/string"
        val jsonPath = "/jsonresponses/factionResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .get("/api/factions/string")
                .then()
                .statusCode(200)
                .body("name", `is`("string"))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/factions/string", 1)
    }
}