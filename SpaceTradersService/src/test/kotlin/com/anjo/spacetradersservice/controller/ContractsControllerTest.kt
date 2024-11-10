package com.anjo.spacetradersservice.controller

import com.anjo.spacetradersservice.TestcontainersConfiguration
import com.anjo.spacetradersservice.generated.infrastructure.RequestMethod.GET
import com.anjo.spacetradersservice.generated.infrastructure.RequestMethod.POST
import com.anjo.spacetradersservice.generated.models.DeliverContractRequest
import com.anjo.spacetradersservice.generated.models.TradeSymbol
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
class ContractsControllerTest {
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
    fun `given auth token when getMyContracts then return response`() {
        //given
        val token = UUID.randomUUID().toString()
        val path = "/my/contracts"
        val jsonPath = "/jsonresponses/contract/contractsResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given().contentType(ContentType.JSON)
                .header("token", token)
                .get("/api/contracts")
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("meta", notNullValue())

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/contracts", 1)
    }

    @Test
    fun `given auth token when getMyContract then return response`() {
        //given
        val token = UUID.randomUUID().toString()
        val contractId = UUID.randomUUID().toString()
        val path = "/my/contracts/$contractId"
        val jsonPath = "/jsonresponses/contract/contractResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, GET, path, jsonPath)

        //when
        given().contentType(ContentType.JSON)
                .header("token", token)
                .get("/api/contracts/$contractId")
                .then()
                .statusCode(200)
                .body("id", `is`("string"))

        //then
        mockServerClient.verifyMockServerRequest(GET.name, "/my/contracts/$contractId", 1)
    }

    @Test
    fun `given auth token when acceptContract then return response`() {
        //given
        val token = UUID.randomUUID().toString()
        val contractId = UUID.randomUUID().toString()
        val path = "/my/contracts/$contractId/accept"
        val jsonPath = "/jsonresponses/contract/acceptContractResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given().contentType(ContentType.JSON)
                .header("token", token)
                .post("/api/contracts/$contractId/accept")
                .then()
                .statusCode(200)
                .body("id", `is`("string"))

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/contracts/$contractId/accept", 1)
    }

    @Test
    fun `given auth token and deliverRequest when deliverCargo then return response`() {
        //given
        val token = UUID.randomUUID().toString()
        val contractId = UUID.randomUUID().toString()
        val body = DeliverContractRequest(UUID.randomUUID().toString(), TradeSymbol.HYDROCARBON.toString(), 1)
        val path = "/my/contracts/$contractId/deliver"
        val jsonPath = "/jsonresponses/contract/deliverContractResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given().contentType(ContentType.JSON)
                .header("token", token)
                .body(body)
                .post("/api/contracts/$contractId/deliver-cargo")
                .then()
                .statusCode(200)
                .body("id", `is`("string"))

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/contracts/$contractId/deliver", 1)
    }

    @Test
    fun `given auth token and deliverRequest when deliverCargo and fullfil contract then return response`() {
        //given
        val token = UUID.randomUUID().toString()
        val contractId = UUID.randomUUID().toString()
        val body = DeliverContractRequest(UUID.randomUUID().toString(), TradeSymbol.HYDROCARBON.toString(), 1)
        val path = "/my/contracts/$contractId/fulfill"
        val jsonPath = "/jsonresponses/contract/deliverContractResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        val deliverPath = "/my/contracts/$contractId/deliver"
        val fulfillJsonPath = "/jsonresponses/contract/fulfilContractResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, deliverPath, fulfillJsonPath)

        //when
        given().contentType(ContentType.JSON)
                .header("token", token)
                .body(body)
                .post("/api/contracts/$contractId/deliver-cargo")
                .then()
                .statusCode(200)
                .body("id", `is`("string"))

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/contracts/$contractId/deliver", 1)
        mockServerClient.verifyMockServerRequest(POST.name, "/my/contracts/$contractId/fulfill", 1)
    }

    @Test
    fun `given auth token and deliverRequest when deliverCargo with null then return response`() {
        //given
        val token = UUID.randomUUID().toString()
        val contractId = UUID.randomUUID().toString()
        val body = DeliverContractRequest(UUID.randomUUID().toString(), TradeSymbol.HYDROCARBON.toString(), 1)
        val path = "/my/contracts/$contractId/deliver"
        val jsonPath = "/jsonresponses/contract/deliverNullContractResponse.json"

        mockExternalQueryWithAuth(mockServerClient, token, POST, path, jsonPath)

        //when
        given().contentType(ContentType.JSON)
                .header("token", token)
                .body(body)
                .post("/api/contracts/$contractId/deliver-cargo")
                .then()
                .statusCode(200)
                .body("id", `is`("string"))

        //then
        mockServerClient.verifyMockServerRequest(POST.name, "/my/contracts/$contractId/deliver", 1)
    }
}