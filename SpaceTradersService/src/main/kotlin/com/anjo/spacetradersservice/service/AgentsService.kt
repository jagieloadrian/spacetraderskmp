package com.anjo.spacetradersservice.service

import com.anjo.spacetradersservice.generated.apis.AgentsApi
import com.anjo.spacetradersservice.generated.models.Agent
import com.anjo.spacetradersservice.generated.models.AgentsResponse
import com.anjo.spacetradersservice.utils.prepareToken
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service

@Service
class AgentsService(private val agentsApi: AgentsApi) {

    suspend fun getAgents(page: Int): AgentsResponse {
        return agentsApi.getAgents(page, 20)
                .awaitFirst()
    }

    suspend fun getExternalAgent(agentId: String): Agent {
        return agentsApi.getAgent(agentId)
                .awaitSingle()
                .data
    }

    suspend fun getMyAgent(token: String): Agent {
        return agentsApi.getMyAgent(prepareToken(token))
                .awaitSingle()
                .data
    }
}