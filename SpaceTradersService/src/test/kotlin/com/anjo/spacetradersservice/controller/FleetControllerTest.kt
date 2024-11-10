package com.anjo.spacetradersservice.controller

import com.anjo.spacetradersservice.TestcontainersConfiguration
import com.anjo.spacetradersservice.generated.infrastructure.RequestMethod.GET
import com.anjo.spacetradersservice.generated.infrastructure.RequestMethod.PATCH
import com.anjo.spacetradersservice.generated.infrastructure.RequestMethod.POST
import com.anjo.spacetradersservice.generated.models.ExtractResourcesRequest
import com.anjo.spacetradersservice.generated.models.InstallMountRequest
import com.anjo.spacetradersservice.generated.models.JettisonRequest
import com.anjo.spacetradersservice.generated.models.JumpShipRequest
import com.anjo.spacetradersservice.generated.models.NavigateShipRequest
import com.anjo.spacetradersservice.generated.models.PatchShipNavRequest
import com.anjo.spacetradersservice.generated.models.PurchaseCargoRequest
import com.anjo.spacetradersservice.generated.models.PurchaseShipRequest
import com.anjo.spacetradersservice.generated.models.RefuelShipRequest
import com.anjo.spacetradersservice.generated.models.RemoveMountRequest
import com.anjo.spacetradersservice.generated.models.SellCargoRequest
import com.anjo.spacetradersservice.generated.models.ShipNavFlightMode
import com.anjo.spacetradersservice.generated.models.ShipRefineRequest
import com.anjo.spacetradersservice.generated.models.ShipType
import com.anjo.spacetradersservice.generated.models.Survey
import com.anjo.spacetradersservice.generated.models.SurveyDeposit
import com.anjo.spacetradersservice.generated.models.TradeSymbol
import com.anjo.spacetradersservice.generated.models.TransferCargoRequest
import com.anjo.spacetradersservice.testutils.Mapper
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
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.test.Test

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FleetControllerTest {
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
    fun `given mock response when getMyShips then return response`() {
        //given
        val token = UUID.randomUUID().toString()
        val path = "/my/ships"
        val jsonPath = "/jsonresponses/fleet/shipsResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .get("/api/fleet")
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("meta", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships", 1)
    }

    @Test
    fun `given mock response when getMyShip then return response`() {
        //given
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/string"
        val jsonPath = "/jsonresponses/fleet/shipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .get("/api/fleet/string")
                .then()
                .statusCode(200)
                .body("symbol", `is`(notNullValue()))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/string", 1)
    }

    @Test
    fun `given mock response when purchaseShip then return response`() {
        //given
        val token = UUID.randomUUID().toString()
        val requestBody = PurchaseShipRequest(shipType = ShipType.SURVEYOR, waypointSymbol = "Waypoint")
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships"
        val jsonPath = "/jsonresponses/fleet/purchaseShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet")
                .then()
                .statusCode(200)
                .body("ship", notNullValue())
                .body("agent", notNullValue())
                .body("transaction", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships", 1)
    }

    @Test
    fun `given mock response when orbitMyShip then return response`() {
        //given
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/string/orbit"
        val jsonPath = "/jsonresponses/fleet/shipNavResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/string/orbit")
                .then()
                .statusCode(200)
                .body("systemSymbol", `is`(notNullValue()))

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/string/orbit", 1)
    }

    @Test
    fun `given mock response when refineOnShip then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = ShipRefineRequest(ShipRefineRequest.Produce.FUEL)
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/shipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        val refinePath = "/my/ships/$shipId/refine"
        val refineJsonPath = "/jsonresponses/fleet/shipRefineResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, refinePath, refineJsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/refine-on-ship")
                .then()
                .statusCode(200)
                .body("cargo", notNullValue())
                .body("cooldown", notNullValue())
                .body("produced", notNullValue())
                .body("consumed", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/refine", 1)
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when createChart then return response`() {
        //given
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/string/chart"
        val jsonPath = "/jsonresponses/fleet/shipChartResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/string/create-chart")
                .then()
                .statusCode(200)
                .body("chart", notNullValue())
                .body("waypoint", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/string/chart", 1)
    }

    @Test
    fun `given mock response when getShipCooldown then return response`() {
        //given
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/string/cooldown"
        val jsonPath = "/jsonresponses/fleet/cooldownResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .get("/api/fleet/string/cooldown")
                .then()
                .statusCode(200)
                .body("shipSymbol", notNullValue())
                .body("totalSeconds", notNullValue())
                .body("remainingSeconds", notNullValue())
                .body("expiration", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/string/cooldown", 1)
    }

    @Test
    fun `given mock response when dockShip then return response`() {
        //given
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/string/dock"
        val jsonPath = "/jsonresponses/fleet/shipNavResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/string/dock")
                .then()
                .statusCode(200)
                .body("waypointSymbol", notNullValue())
                .body("systemSymbol", notNullValue())
                .body("route", notNullValue())
                .body("status", notNullValue())
                .body("flightMode", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/string/dock", 1)
    }

    @Test
    fun `given mock response when createSurvey then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/shipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        val surveyPath = "/my/ships/$shipId/survey"
        val surveyJsonPath = "/jsonresponses/fleet/surveyShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, surveyPath, surveyJsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/$shipId/create-survey")
                .then()
                .statusCode(200)
                .body("cooldown", notNullValue())
                .body("surveys", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/survey", 1)
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when extractResource then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/shipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        val extractPath ="/my/ships/$shipId/extract"
        val extractJsonPath = "/jsonresponses/fleet/extractShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, extractPath, extractJsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/$shipId/extract-resource")
                .then()
                .statusCode(200)
                .body("cooldown", notNullValue())
                .body("extraction", notNullValue())
                .body("cargo", notNullValue())
                .body("events", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/extract", 1)
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when extractGas then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/shipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        val siphonPath ="/my/ships/$shipId/siphon"
        val siphonJsonPath = "/jsonresponses/fleet/siphonShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, siphonPath, siphonJsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/$shipId/extract-gas")
                .then()
                .statusCode(200)
                .body("cooldown", notNullValue())
                .body("siphon", notNullValue())
                .body("cargo", notNullValue())
                .body("events", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/siphon", 1)
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when extractFromWaypoint then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val survey = Survey("signature", "symbol", listOf(SurveyDeposit("symbol")),
                OffsetDateTime.now(), Survey.PropertySize.LARGE)
        val requestBody = ExtractResourcesRequest(survey)
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/shipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        val extractPath ="/my/ships/$shipId/extract/survey"
        val extractJsonPath = "/jsonresponses/fleet/extractShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, extractPath, extractJsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/extract-from-waypoint")
                .then()
                .statusCode(200)
                .body("cooldown", notNullValue())
                .body("extraction", notNullValue())
                .body("cargo", notNullValue())
                .body("events", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/extract/survey", 1)
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when throwAwayResources then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = JettisonRequest(TradeSymbol.FUEL, 1)
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId/jettison"
        val jsonPath = "/jsonresponses/fleet/shipCargoResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/jettison-resources")
                .then()
                .statusCode(200)
                .body("capacity", notNullValue())
                .body("units", notNullValue())
                .body("inventory", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/jettison", 1)
    }

    @Test
    fun `given mock response when jumpByShip then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = JumpShipRequest("waypoint")
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/shipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        val jumpPath = "/my/ships/$shipId/jump"
        val jumpJsonPath = "/jsonresponses/fleet/jumpShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, jumpPath, jumpJsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/jump")
                .then()
                .statusCode(200)
                .body("nav", notNullValue())
                .body("cooldown", notNullValue())
                .body("transaction", notNullValue())
                .body("agent", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/jump", 1)
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when navigate then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = NavigateShipRequest("waypoint")
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val navigatePath = "/my/ships/$shipId/navigate"
        val navigateJsonPath = "/jsonresponses/fleet/navShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, navigatePath, navigateJsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/navigate")
                .then()
                .statusCode(200)
                .body("nav", notNullValue())
                .body("fuel", notNullValue())
                .body("events", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/navigate", 1)
    }

    @Test
    fun `given mock response when changeFlightMode then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = PatchShipNavRequest(ShipNavFlightMode.STEALTH)
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId/nav"
        val jsonPath = "/jsonresponses/fleet/shipNavigationResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, PATCH, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .`when`()
                .patch("/api/fleet/$shipId/flight-mode")
                .then()
                .statusCode(200)
                .body("systemSymbol", notNullValue())
                .body("waypointSymbol", notNullValue())
                .body("route", notNullValue())
                .body("status", notNullValue())
                .body("flightMode", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(PATCH.name, "/my/ships/$shipId/nav", 1)
    }

    @Test
    fun `given mock response when getNavigation then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = PatchShipNavRequest(ShipNavFlightMode.STEALTH)
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId/nav"
        val jsonPath = "/jsonresponses/fleet/shipNavigationResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .get("/api/fleet/$shipId/navigation")
                .then()
                .statusCode(200)
                .body("systemSymbol", notNullValue())
                .body("waypointSymbol", notNullValue())
                .body("route", notNullValue())
                .body("status", notNullValue())
                .body("flightMode", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId/nav", 1)
    }

    @Test
    fun `given mock response when warpShip then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = NavigateShipRequest("waypoint")
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/shipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        val warpPath = "/my/ships/$shipId/warp"
        val warpJsonPath = "/jsonresponses/fleet/warpShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, warpPath, warpJsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/warp")
                .then()
                .statusCode(200)
                .body("nav", notNullValue())
                .body("fuel", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/warp", 1)
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when sellCargo then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = SellCargoRequest(TradeSymbol.FUEL, 1)
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/dockedShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        val waypointPath = "/systems/string/waypoints/string"
        val waypointJsonPath = "/jsonresponses/system/systemWaypointResponse.json"

        mockExternalQuery(mockServerClient, GET, waypointPath, waypointJsonPath)

        val sellPath = "/my/ships/$shipId/sell"
        val sellJsonPath = "/jsonresponses/fleet/sellShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, sellPath, sellJsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/sell-cargo")
                .then()
                .statusCode(200)
                .body("agent", notNullValue())
                .body("cargo", notNullValue())
                .body("transaction", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/sell", 1)
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
        mockServerClient.verifyMockServerRequest(GET.name, "/systems/string/waypoints/string", 1)
    }

    @Test
    fun `given mock response when scanSystem then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/shipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        val scanPath = "/my/ships/$shipId/scan/systems"
        val scanJsonPath = "/jsonresponses/fleet/scanSystemResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, scanPath, scanJsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/$shipId/scan-system")
                .then()
                .statusCode(200)
                .body("cooldown", notNullValue())
                .body("systems", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/scan/systems", 1)
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when scanWaypoints then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/shipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        val scanPath = "/my/ships/$shipId/scan/waypoints"
        val scanJsonPath = "/jsonresponses/fleet/scanWaypointsResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, scanPath, scanJsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/$shipId/scan-waypoints")
                .then()
                .statusCode(200)
                .body("cooldown", notNullValue())
                .body("waypoints", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/scan/waypoints", 1)
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when scanShips then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/shipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        val scanPath = "/my/ships/$shipId/scan/ships"
        val scanJsonPath = "/jsonresponses/fleet/scanShipsResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, scanPath, scanJsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/$shipId/scan-ships")
                .then()
                .statusCode(200)
                .body("cooldown", notNullValue())
                .body("ships", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/scan/ships", 1)
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when refuelShip then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = RefuelShipRequest(2, true)
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId/refuel"
        val jsonPath = "/jsonresponses/fleet/refuelShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/refuel-ship")
                .then()
                .statusCode(200)
                .body("agent", notNullValue())
                .body("fuel", notNullValue())
                .body("transaction", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/refuel", 1)
    }

    @Test
    fun `given mock response when purchaseCargo then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = PurchaseCargoRequest(TradeSymbol.FOOD, 2)
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId/purchase"
        val jsonPath = "/jsonresponses/fleet/purchaseCargoResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/purchase-cargo")
                .then()
                .statusCode(200)
                .body("agent", notNullValue())
                .body("cargo", notNullValue())
                .body("transaction", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/purchase", 1)
    }

    @Test
    fun `given mock response when transferCargo then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = TransferCargoRequest(TradeSymbol.FOOD, 2, shipId)
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId/transfer"
        val jsonPath = "/jsonresponses/fleet/shipCargoResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/transfer-cargo")
                .then()
                .statusCode(200)
                .body("capacity", notNullValue())
                .body("units", notNullValue())
                .body("inventory", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/transfer", 1)
    }

    @Test
    fun `given mock response when negotiateContract then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId/negotiate/contract"
        val jsonPath = "/jsonresponses/contract/negotiateContractResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/$shipId/negotiate-contract")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("factionSymbol", notNullValue())
                .body("type", notNullValue())
                .body("terms", notNullValue())
                .body("accepted", notNullValue())
                .body("fulfilled", notNullValue())
                .body("expiration", notNullValue())
                .body("deadlineToAccept", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/negotiate/contract", 1)
    }

    @Test
    fun `given mock response when getShipMounts then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId/mounts"
        val jsonPath = "/jsonresponses/fleet/mountShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .get("/api/fleet/$shipId/mount")
                .then()
                .statusCode(200)
                .body( "size()", `is`(1))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId/mounts", 1)
    }

    @Test
    fun `given mock response when installMount then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = InstallMountRequest("FUEL")
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId/mounts/install"
        val jsonPath = "/jsonresponses/fleet/installMountResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/mount")
                .then()
                .statusCode(200)
                .body("agent", notNullValue())
                .body("mounts", notNullValue())
                .body("cargo", notNullValue())
                .body("transaction", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/mounts/install", 1)
    }

    @Test
    fun `given mock response when removeMount then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = RemoveMountRequest("FUEL")
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId/mounts/remove"
        val jsonPath = "/jsonresponses/fleet/installMountResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .delete("/api/fleet/$shipId/mount")
                .then()
                .statusCode(200)
                .body("agent", notNullValue())
                .body("mounts", notNullValue())
                .body("cargo", notNullValue())
                .body("transaction", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/mounts/remove", 1)
    }

    @Test
    fun `given mock response when getPossibleValueFromScrapShip then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId/scrap"
        val jsonPath = "/jsonresponses/fleet/scrapTransactionResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .get("/api/fleet/$shipId/scrap")
                .then()
                .statusCode(200)
                .body("waypointSymbol", notNullValue())
                .body("shipSymbol", notNullValue())
                .body("totalPrice", notNullValue())
                .body("timestamp", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId/scrap", 1)
    }

    @Test
    fun `given mock response when scrapShip then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId/scrap"
        val jsonPath = "/jsonresponses/fleet/scrapShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/$shipId/scrap")
                .then()
                .statusCode(200)
                .body("agent", notNullValue())
                .body("transaction", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/scrap", 1)
    }

    @Test
    fun `given mock response when getCostOfRepair then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId/repair"
        val jsonPath = "/jsonresponses/fleet/scrapTransactionResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .get("/api/fleet/$shipId/repair")
                .then()
                .statusCode(200)
                .body("waypointSymbol", notNullValue())
                .body("shipSymbol", notNullValue())
                .body("totalPrice", notNullValue())
                .body("timestamp", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId/repair", 1)
    }

    @Test
    fun `given mock response when repairShip then return response`() {
        //given
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId/repair"
        val jsonPath = "/jsonresponses/fleet/repairShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/$shipId/repair")
                .then()
                .statusCode(200)
                .body("agent", notNullValue())
                .body("transaction", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/ships/$shipId/repair", 1)
    }

    @Test
    fun `given mock response when refineOnShip then throw error`() {
        //given
        val errorMessage = "This ship does not have required modules"
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = ShipRefineRequest(ShipRefineRequest.Produce.FUEL)
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/nonValidShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/refine-on-ship")
                .then()
                .statusCode(400)
                .body("errorMessage", `is`(errorMessage))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when createSurvey then throw error`() {
        //given
        val errorMessage = "This ship does not have required mounts"
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/nonValidShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/$shipId/create-survey")
                .then()
                .statusCode(400)
                .body("errorMessage", `is`(errorMessage))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when extractGas then throw error`() {
        //given
        val errorMessage = "This ship does not have required mounts/modules"
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/nonValidShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/$shipId/extract-gas")
                .then()
                .statusCode(400)
                .body("errorMessage", `is`(errorMessage))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response and nonValid request when extractFromWaypoint then throw error`() {
        //given
        val errorMessage = "This endpoint required properly request body"
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = ExtractResourcesRequest(null)
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/shipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/extract-from-waypoint")
                .then()
                .statusCode(400)
                .body("errorMessage", `is`(errorMessage))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response and valid request when extractFromWaypoint then throw error`() {
        //given
        val errorMessage = "This ship does not have required mounts"
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val survey = Survey("signature", "symbol", listOf(SurveyDeposit("symbol")),
                OffsetDateTime.now(), Survey.PropertySize.LARGE)
        val requestBody = ExtractResourcesRequest(survey)
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/nonValidShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/extract-from-waypoint")
                .then()
                .statusCode(400)
                .body("errorMessage", `is`(errorMessage))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when jumpByShip then throw error`() {
        //given
        val errorMessage = "This ship isn't in orbit state"
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = JumpShipRequest("waypoint")
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/nonValidShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/jump")
                .then()
                .statusCode(400)
                .body("errorMessage", `is`(errorMessage))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when warpShip then throw error`() {
        //given
        val errorMessage = "This ship does not have required modules"
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = NavigateShipRequest("waypoint")
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/nonValidShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/warp")
                .then()
                .statusCode(400)
                .body("errorMessage", `is`(errorMessage))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when sellCargo then throw error`() {
        //given
        val errorMessage = "This ship is not in marketplace waypoint"
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val requestBody = SellCargoRequest(TradeSymbol.FUEL, 1)
        val jsonRequestBody = Mapper.writeAsString(requestBody)
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/shipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .body(jsonRequestBody)
                .post("/api/fleet/$shipId/sell-cargo")
                .then()
                .statusCode(400)
                .body("errorMessage", `is`(errorMessage))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when scanSystem then throw error`() {
        //given
        val errorMessage = "This ship does not have required mounts"
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/nonValidShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/$shipId/scan-system")
                .then()
                .statusCode(400)
                .body("errorMessage", `is`(errorMessage))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when scanWaypoints then throw error`() {
        //given
        val errorMessage = "This ship does not have required mounts"
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/nonValidShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/$shipId/scan-waypoints")
                .then()
                .statusCode(400)
                .body("errorMessage", `is`(errorMessage))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }


    @Test
    fun `given mock response when scanShips then throw error`() {
        //given
        val errorMessage = "This ship does not have required mounts"
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/nonValidShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/$shipId/scan-ships")
                .then()
                .statusCode(400)
                .body("errorMessage", `is`(errorMessage))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }

    @Test
    fun `given mock response when extractResource then throw error`() {
        //given
        val errorMessage = "This ship does not have required mounts"
        val shipId = "shipId"
        val token = UUID.randomUUID().toString()
        val path = "/my/ships/$shipId"
        val jsonPath = "/jsonresponses/fleet/nonValidShipResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given()
                .contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/fleet/$shipId/extract-resource")
                .then()
                .statusCode(400)
                .body("errorMessage", `is`(errorMessage))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/ships/$shipId", 1)
    }
}