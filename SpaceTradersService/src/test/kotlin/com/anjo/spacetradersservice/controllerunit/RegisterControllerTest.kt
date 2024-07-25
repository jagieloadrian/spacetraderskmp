package com.anjo.spacetradersservice.controllerunit

import com.anjo.spacetradersservice.controller.RegisterController
import com.anjo.spacetradersservice.generated.models.Agent
import com.anjo.spacetradersservice.generated.models.Contract
import com.anjo.spacetradersservice.generated.models.ContractPayment
import com.anjo.spacetradersservice.generated.models.ContractTerms
import com.anjo.spacetradersservice.generated.models.Cooldown
import com.anjo.spacetradersservice.generated.models.Faction
import com.anjo.spacetradersservice.generated.models.FactionSymbol
import com.anjo.spacetradersservice.generated.models.RegisterRequest
import com.anjo.spacetradersservice.generated.models.RegisterResponseData
import com.anjo.spacetradersservice.generated.models.Ship
import com.anjo.spacetradersservice.generated.models.ShipCargo
import com.anjo.spacetradersservice.generated.models.ShipCrew
import com.anjo.spacetradersservice.generated.models.ShipEngine
import com.anjo.spacetradersservice.generated.models.ShipFrame
import com.anjo.spacetradersservice.generated.models.ShipFuel
import com.anjo.spacetradersservice.generated.models.ShipNav
import com.anjo.spacetradersservice.generated.models.ShipNavRoute
import com.anjo.spacetradersservice.generated.models.ShipNavRouteWaypoint
import com.anjo.spacetradersservice.generated.models.ShipNavStatus
import com.anjo.spacetradersservice.generated.models.ShipReactor
import com.anjo.spacetradersservice.generated.models.ShipRegistration
import com.anjo.spacetradersservice.generated.models.ShipRequirements
import com.anjo.spacetradersservice.generated.models.ShipRole
import com.anjo.spacetradersservice.generated.models.WaypointType
import com.anjo.spacetradersservice.service.RegisterService
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class RegisterControllerTest {
    @RelaxedMockK
    lateinit var registerService: RegisterService

    @InjectMockKs
    lateinit var registerController: RegisterController

    @Test
    fun `given registerRequest when registerNewUser then return RegisterResponseData`(): Unit = runTest {
        //given
        val agent = Agent("agent", headquarters = "head",
                credits = 1L, startingFaction = "fraction", shipCount = 1, accountId = "accountId")
        val contract = Contract(id = "ContractId", factionSymbol = "faction", type = Contract.Type.PROCUREMENT,
                terms = ContractTerms(deadline = OffsetDateTime.MIN, payment = ContractPayment(10, 10),
                        deliver = listOf()), expiration = OffsetDateTime.MAX)
        val faction = Faction(FactionSymbol.CULT, "name", "desc", "head", listOf(), false)
        val shipNavRouteWaypoint = ShipNavRouteWaypoint("symbol", WaypointType.NEBULA, "cm", 0, 0)
        val route =
            ShipNavRoute(shipNavRouteWaypoint, origin = shipNavRouteWaypoint, departureTime = OffsetDateTime.MAX,
                    arrival = OffsetDateTime.MAX)
        val ship = Ship("symbol", ShipRegistration(name = "name", factionSymbol = "symbol",
                role = ShipRole.TRANSPORT), ShipNav(systemSymbol = "symbol", waypointSymbol = "symbol",
                route = route, status = ShipNavStatus.DOCKED), ShipCrew(current = 0, required = 0, capacity = 0,
                rotation = ShipCrew.Rotation.RELAXED, morale = 0, wages = 0),
                ShipFrame(symbol = ShipFrame.Symbol.SHUTTLE,
                        name = "name", description = "desc", condition = 0.0, integrity = 0.0, moduleSlots = 0,
                        mountingPoints = 0,
                        fuelCapacity = 0, requirements = ShipRequirements()), ShipReactor(
                symbol = ShipReactor.Symbol.SOLAR_I, name = "name", description = "desc",
                condition = 0.0, integrity = 0.0, powerOutput = 0, requirements = ShipRequirements()),
                engine = ShipEngine(symbol = ShipEngine.Symbol.ION_DRIVE_I, name = "name", description = "desc",
                        condition = 0.0, integrity = 0.0, speed = 0, requirements = ShipRequirements()),
                cooldown = Cooldown(shipSymbol = "symbol", totalSeconds = 0, remainingSeconds = 0),
                modules = listOf(),
                mounts = listOf(),
                cargo = ShipCargo(capacity = 0, units = 0, inventory = listOf()),
                fuel = ShipFuel(0, 0)
        )
        val expected = RegisterResponseData(agent = agent,
                contract = contract, faction = faction, ship = ship, token = "token")

        val registerRequest = RegisterRequest(faction = FactionSymbol.CULT, "symbol", "email@example.com")

        coEvery { registerService.registerNewUser(registerRequest, any()) } returns expected

        //when
        val actual = registerController.register(registerRequest, LocalDateTime.now())
        testScheduler.advanceUntilIdle()

        //then
        actual shouldBe expected

    }
}