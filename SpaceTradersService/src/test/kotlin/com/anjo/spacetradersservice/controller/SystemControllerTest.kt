package com.anjo.spacetradersservice.controller

import com.anjo.spacetradersservice.TestcontainersConfiguration
import com.anjo.spacetradersservice.generated.infrastructure.RequestMethod.GET
import com.anjo.spacetradersservice.generated.infrastructure.RequestMethod.POST
import com.anjo.spacetradersservice.generated.models.SupplyConstructionRequest
import com.anjo.spacetradersservice.generated.models.WaypointType
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
class SystemControllerTest {
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
    fun `given mock response when getAllSystems then return response`() {
        //given
        val path = "/systems"
        val jsonPath = "/jsonresponses/system/systemsResponse.json"

        mockExternalQuery(mockServerClient, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .get("/api/solar-system")
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("meta", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/systems", 1)
    }

    @Test
    fun `given mock response when getSystem then return response`() {
        //given
        val path = "/systems/string"
        val jsonPath = "/jsonresponses/system/systemResponse.json"

        mockExternalQuery(mockServerClient, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .get("/api/solar-system/string")
                .then()
                .statusCode(200)
                .body("symbol", `is`(notNullValue()))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, path, 1)
    }

    @Test
    fun `given mock response when getSystemWaypoints then return response`() {
        //given
        val path = "/systems/string/waypoints"
        val jsonPath = "/jsonresponses/system/systemWaypointsResponse.json"

        mockExternalQuery(mockServerClient, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .queryParam("type", WaypointType.PLANET)
                .get("/api/solar-system/string/waypoints")
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("meta", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(GET.name, path, 1)
    }

    @Test
    fun `given mock response when getSystemWaypoint then return response`() {
        //given
        val path = "/systems/string/waypoints/waypoint"
        val jsonPath = "/jsonresponses/system/systemWaypointResponse.json"

        mockExternalQuery(mockServerClient, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .get("/api/solar-system/string/waypoints/waypoint")
                .then()
                .statusCode(200)
                .body("symbol", `is`(notNullValue()))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, path, 1)
    }

    @Test
    fun `given mock response when getMarket then return response`() {
        //given
        val path = "/systems/string/waypoints/waypoint/market"
        val jsonPath = "/jsonresponses/system/systemMarketResponse.json"

        mockExternalQuery(mockServerClient, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .get("/api/solar-system/string/waypoints/waypoint/market")
                .then()
                .statusCode(200)
                .body("symbol", `is`(notNullValue()))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, path, 1)
    }

    @Test
    fun `given mock response when getShipyard then return response`() {
        //given
        val path = "/systems/string/waypoints/waypoint/shipyard"
        val jsonPath = "/jsonresponses/system/systemShipyardResponse.json"

        mockExternalQuery(mockServerClient, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .get("/api/solar-system/string/waypoints/waypoint/shipyard")
                .then()
                .statusCode(200)
                .body("symbol", `is`(notNullValue()))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, path, 1)
    }

    @Test
    fun `given mock response when getJumpGate then return response`() {
        //given
        val path = "/systems/string/waypoints/waypoint/jump-gate"
        val jsonPath = "/jsonresponses/system/systemJumpResponse.json"

        mockExternalQuery(mockServerClient, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .get("/api/solar-system/string/waypoints/waypoint/jump-gate")
                .then()
                .statusCode(200)
                .body("symbol", `is`(notNullValue()))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, path, 1)
    }

    @Test
    fun `given mock response when getConstruction then return response`() {
        //given
        val path = "/systems/string/waypoints/waypoint/construction"
        val jsonPath = "/jsonresponses/system/systemConstructionResponse.json"

        mockExternalQuery(mockServerClient, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .get("/api/solar-system/string/waypoints/waypoint/construction")
                .then()
                .statusCode(200)
                .body("symbol", `is`(notNullValue()))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, path, 1)
    }

    @Test
    fun `given mock response when supplyConstruction then return response`() {
        //given
        val path = "/systems/string/waypoints/waypoint"
        val token = UUID.randomUUID().toString()
        val body = SupplyConstructionRequest("string", "trade", 1)
        val jsonPath = "/jsonresponses/system/systemWaypointResponse.json"

        mockExternalQuery(mockServerClient, GET, path, jsonPath)
        val supplyPath = "/systems/string/waypoints/waypoint/construction/supply"
        val supplyJsonPath = "/jsonresponses/system/systemSupplyConstructionResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, supplyPath, supplyJsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .header("token", token)
                .post("/api/solar-system/string/waypoints/waypoint/construction")
                .then()
                .statusCode(200)
                .body("construction", notNullValue())
                .body("cargo", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, supplyPath, 1)
        mockServerClient.verifyMockServerRequest(GET.name, path, 1)
    }

    @Test
    fun `given mock response when supplyConstruction and is not under construction then throw error`() {
        //given
        val errorMessage = "Waypoint is not under construction"
        val path = "/systems/string/waypoints/waypoint"
        val token = UUID.randomUUID().toString()
        val body = SupplyConstructionRequest("string", "trade", 1)
        val jsonPath = "/jsonresponses/system/systemWaypointNotUnderConstResponse.json"

        mockExternalQuery(mockServerClient, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .header("token", token)
                .post("/api/solar-system/string/waypoints/waypoint/construction")
                .then()
                .statusCode(400)
                .body("errorMessage", `is`(errorMessage))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, path, 1)
    }
}