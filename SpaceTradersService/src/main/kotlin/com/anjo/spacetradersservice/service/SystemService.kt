package com.anjo.spacetradersservice.service

import com.anjo.spacetradersservice.exception.NonValidActionException
import com.anjo.spacetradersservice.generated.apis.SystemsApi
import com.anjo.spacetradersservice.generated.models.Construction
import com.anjo.spacetradersservice.generated.models.ConstructionSupplyResponseData
import com.anjo.spacetradersservice.generated.models.JumpGate
import com.anjo.spacetradersservice.generated.models.Market
import com.anjo.spacetradersservice.generated.models.Shipyard
import com.anjo.spacetradersservice.generated.models.SupplyConstructionRequest
import com.anjo.spacetradersservice.generated.models.System
import com.anjo.spacetradersservice.generated.models.SystemResponse
import com.anjo.spacetradersservice.generated.models.SystemWaypointsResponse
import com.anjo.spacetradersservice.generated.models.Waypoint
import com.anjo.spacetradersservice.generated.models.WaypointTraitSymbol
import com.anjo.spacetradersservice.generated.models.WaypointType
import com.anjo.spacetradersservice.utils.prepareToken
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service

@Service
class SystemService(private val systemsApi: SystemsApi) {

    suspend fun getAllSystems(page: Int): SystemResponse {
        return systemsApi.getSystems(page)
                .awaitSingle()
    }

    suspend fun getSystem(systemId: String): System {
        return systemsApi.getSystem(systemId)
                .awaitSingle()
                .data
    }

    suspend fun getSystemWaypoints(
            systemId: String, page: Int, type: WaypointType,
            traits: List<WaypointTraitSymbol>,
    ): SystemWaypointsResponse {
        return systemsApi.getSystemWaypoints(systemSymbol = systemId, page = page, type = type, traits = traits)
                .awaitSingle()
    }

    suspend fun getWaypoint(systemId: String, waypointSymbol: String): Waypoint {
        return systemsApi.getWaypoint(systemId, waypointSymbol)
                .awaitSingle()
                .data
    }

    suspend fun getMarket(systemId: String, waypointSymbol: String): Market {
        return systemsApi.getMarket(systemId, waypointSymbol)
                .awaitSingle()
                .data
    }

    suspend fun getShipyard(systemId: String, waypointSymbol: String): Shipyard {
        return systemsApi.getShipyard(systemId, waypointSymbol)
                .awaitSingle()
                .data
    }

    suspend fun getJumpGate(systemId: String, waypointSymbol: String): JumpGate {
        return systemsApi.getJumpGate(systemId, waypointSymbol)
                .awaitSingle()
                .data
    }

    suspend fun getConstruction(systemId: String, waypointSymbol: String): Construction {
        return systemsApi.getConstruction(systemId, waypointSymbol)
                .awaitSingle()
                .data
    }

    suspend fun supplyConstruction(
            systemId: String, waypointSymbol: String, token: String,
            request: SupplyConstructionRequest,
    ): ConstructionSupplyResponseData {
        return when(isUnderConstruction(systemId, waypointSymbol)) {
            true ->  systemsApi.supplyConstruction(systemId, waypointSymbol, prepareToken(token), request)
                    .awaitSingle()
                    .data
            false -> throw NonValidActionException("Waypoint is not under construction")
        }
    }

    suspend fun isUnderConstruction(systemId: String, waypointSymbol: String): Boolean {
        return getWaypoint(systemId, waypointSymbol)
                .isUnderConstruction
    }
}