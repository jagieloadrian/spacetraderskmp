package com.anjo.spacetradersservice.controller

import com.anjo.spacetradersservice.generated.models.DefaultSystemResponse
import com.anjo.spacetradersservice.service.StatusService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/status")
class StatusController(private val statusService: StatusService) {

    @GetMapping
    suspend fun getStatus(): DefaultSystemResponse {
        return statusService.returnSystemInfo()
    }
}