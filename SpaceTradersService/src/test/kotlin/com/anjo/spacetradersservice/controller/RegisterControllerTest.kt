package com.anjo.spacetradersservice.controller

import com.anjo.spacetradersservice.TestcontainersConfiguration
import com.anjo.spacetradersservice.generated.infrastructure.RequestMethod
import com.anjo.spacetradersservice.generated.models.FactionSymbol
import com.anjo.spacetradersservice.generated.models.RegisterRequest
import com.anjo.spacetradersservice.repository.SpaceTraderRepository
import com.anjo.spacetradersservice.testutils.Mapper
import com.anjo.spacetradersservice.testutils.defaultHeader
import com.anjo.spacetradersservice.testutils.readJson
import com.anjo.spacetradersservice.testutils.verifyMockServerRequest
import io.kotest.matchers.collections.shouldHaveSize
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.springframework.beans.factory.annotation.Autowired
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
import java.time.LocalDateTime
import kotlin.test.Test

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RegisterControllerTest {
    @LocalServerPort
    val port: Int = 0
    private lateinit var mockServerClient: MockServerClient
    @Autowired
    private lateinit var spaceTraderRepository: SpaceTraderRepository

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
        spaceTraderRepository.deleteAll()
    }

    @Test
    fun `given mockResponse when call api register then return AgentResponse`(): Unit = runTest {
        //given
        val path = "/register"
        val requestBody = RegisterRequest(faction = FactionSymbol.CULT, "symbol", "email@example.com")
        val jsonRequestBody = Mapper.writeAsString(requestBody)

        mockServerClient.`when`(request().withMethod(RequestMethod.POST.name).withPath(path)
                .withBody(jsonRequestBody))
                .respond(response().withStatusCode(200)
                        .withHeaders(defaultHeader())
                        .withBody(readJson("/jsonresponses/registerResponse.json")))

        //when
        given()
                .contentType(ContentType.JSON)
                .queryParam("nextResetDate", LocalDateTime.now().plusDays(10).toString())
                .body(jsonRequestBody)
                .post("/api/register")
                .then()
                .statusCode(200)
                .body("token", `is`("string"))

        //then
        mockServerClient.verifyMockServerRequest(RequestMethod.POST.name, path, 1)
        spaceTraderRepository.findAll() shouldHaveSize 1
    }

    @Test
    fun `given bad resetDate when call api register then return 400`() {
        //given
        val path = "/register"
        val requestBody = RegisterRequest(faction = FactionSymbol.CULT, "symbol", "email@example.com")
        val jsonRequestBody = Mapper.writeAsString(requestBody)

        //when
        given()
                .contentType(ContentType.JSON)
                .queryParam("nextResetDate", LocalDateTime.now().minusDays(10).toString())
                .body(jsonRequestBody)
                .post("/api/register")
                .then()
                .statusCode(400)
                .body("errorMessage", `is`("Non valid resetDate"))

        //then

        mockServerClient.verifyMockServerRequest(RequestMethod.POST.name, path, 0)
        spaceTraderRepository.findAll() shouldHaveSize 0
    }
}