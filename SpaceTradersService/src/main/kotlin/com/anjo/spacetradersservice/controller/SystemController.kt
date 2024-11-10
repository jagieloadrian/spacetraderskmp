package com.anjo.spacetradersservice.controller

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
import com.anjo.spacetradersservice.service.SystemService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/solar-system")
class SystemController(val systemService: SystemService) {

    @GetMapping
    suspend fun getAllSystems(@RequestParam(required = false) page: Int = 1): SystemResponse {
        return systemService.getAllSystems(page)
    }

    @GetMapping("/{systemId}")
    suspend fun getSystem(@PathVariable systemId: String): System {
        return systemService.getSystem(systemId)
    }

    @GetMapping("/{systemId}/waypoints")
    suspend fun getSystemWaypoints(
            @PathVariable systemId: String,
            @RequestParam(required = false) page: Int = 1,
            @RequestParam(required = false) traits: List<WaypointTraitSymbol> = listOf(),
            @RequestParam(required = true) type: WaypointType): SystemWaypointsResponse {
        return systemService.getSystemWaypoints(systemId, page, type, traits)
    }

    @GetMapping("/{systemId}/waypoints/{waypointId}")
    suspend fun getWaypoint(
            @PathVariable systemId: String,
            @PathVariable waypointId: String): Waypoint {
        return systemService.getWaypoint(systemId, waypointId)
    }

    @GetMapping("/{systemId}/waypoints/{waypointId}/market")
    suspend fun getMarket(
            @PathVariable systemId: String,
            @PathVariable waypointId: String): Market {
        return systemService.getMarket(systemId, waypointId)
    }

    @GetMapping("/{systemId}/waypoints/{waypointId}/shipyard")
    suspend fun getShipYard(
            @PathVariable systemId: String,
            @PathVariable waypointId: String): Shipyard {
        return systemService.getShipyard(systemId, waypointId)
    }

    @GetMapping("/{systemId}/waypoints/{waypointId}/jump-gate")
    suspend fun getJumpGate(
            @PathVariable systemId: String,
            @PathVariable waypointId: String): JumpGate {
        return systemService.getJumpGate(systemId, waypointId)
    }

    @GetMapping("/{systemId}/waypoints/{waypointId}/construction")
    suspend fun getConstruction(
            @PathVariable systemId: String,
            @PathVariable waypointId: String): Construction {
        return systemService.getConstruction(systemId, waypointId)
    }

    @PostMapping("/{systemId}/waypoints/{waypointId}/construction")
    suspend fun supplyConstruction(
            @PathVariable systemId: String,
            @PathVariable waypointId: String,
            @RequestBody request: SupplyConstructionRequest,
            @RequestHeader("token") token: String): ConstructionSupplyResponseData {
        return systemService.supplyConstruction(systemId, waypointId, token, request)
    }
}