package com.anjo.spacetradersservice.service

import com.anjo.spacetradersservice.generated.apis.FactionsApi
import com.anjo.spacetradersservice.generated.models.Faction
import com.anjo.spacetradersservice.generated.models.FactionsResponse
import com.anjo.spacetradersservice.utils.prepareToken
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service

@Service
class FactionService(private val factionsApi: FactionsApi) {

    suspend fun getAllFactions(page: Int): FactionsResponse {
        return factionsApi.getFactions(page)
                .awaitSingle()
    }

    suspend fun getFaction(factionId: String, token: String): Faction {
        return factionsApi.getFaction(factionId, prepareToken(token))
                .awaitSingle()
                .data
    }
}
