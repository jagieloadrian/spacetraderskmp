package com.anjo.spacetradersservice.controllerunit

import com.anjo.spacetradersservice.controller.StatusController
import com.anjo.spacetradersservice.generated.models.DefaultSystemResponse
import com.anjo.spacetradersservice.generated.models.Leaderboards
import com.anjo.spacetradersservice.generated.models.ServerResets
import com.anjo.spacetradersservice.generated.models.Stats
import com.anjo.spacetradersservice.service.StatusService
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class StatusControllerTest {
    @RelaxedMockK
    lateinit var statusService: StatusService

    @InjectMockKs
    lateinit var statusController: StatusController

    @Test
    fun `given status response when getStatus then return properly Status`(): Unit = runTest {
        //given
        val stats = Stats(agents = 1, ships = 1, systems = 1, waypoints = 1)
        val expected =
            DefaultSystemResponse(status = "ok", version = "1.0", resetDate = "2024-01-01", description = "description",
                    stats = stats, leaderboards = Leaderboards(listOf(), listOf()),
                    serverResets = ServerResets(next = "2024-02-01", frequency = "week"), announcements = listOf(),
                    links = listOf())

        coEvery { statusService.returnSystemInfo() } returns expected

        //when
        val actual = statusController.getStatus()
        testScheduler.advanceUntilIdle()

        //then
        actual shouldBe expected
    }
}