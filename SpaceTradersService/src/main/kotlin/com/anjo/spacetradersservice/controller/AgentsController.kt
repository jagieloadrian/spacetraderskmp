package com.anjo.spacetradersservice.controller

import com.anjo.spacetradersservice.generated.models.Agent
import com.anjo.spacetradersservice.generated.models.AgentsResponse
import com.anjo.spacetradersservice.service.AgentsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/agents")
class AgentsController(val agentsService: AgentsService) {

    @GetMapping
    suspend fun getAllAgents(@RequestParam(required = false) page: Int = 1): AgentsResponse {
        return agentsService.getAgents(page)
    }

    @GetMapping("/{agentId}")
    suspend fun findExternalAgent(@PathVariable agentId: String): Agent {
        return agentsService.getExternalAgent(agentId)
    }

    @GetMapping("/me")
    suspend fun getMyAgent(@RequestHeader(value = "token") token: String): Agent {
        return agentsService.getMyAgent(token)
    }
}