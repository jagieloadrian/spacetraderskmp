package com.anjo.spacetradersservice.service

import com.anjo.spacetradersservice.generated.apis.FleetApi
import com.anjo.spacetradersservice.generated.apis.SystemsApi
import com.anjo.spacetradersservice.generated.models.Ship
import com.anjo.spacetradersservice.generated.models.ShipModule
import com.anjo.spacetradersservice.generated.models.ShipMount
import com.anjo.spacetradersservice.generated.models.ShipNavStatus
import com.anjo.spacetradersservice.generated.models.WaypointTraitSymbol
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service

@Service
class ShipActionValidatorService(val fleetApi: FleetApi, val systemsApi: SystemsApi) {

    suspend fun hasRequiredModule(shipId: String, token: String, requiredModules: List<ShipModule.Symbol>): Boolean {
        return getShip(token, shipId)
                .modules
                .map { it.symbol }
                .any { requiredModules.contains(it) }
    }

    suspend fun hasRequiredModule(ship: Ship, requiredModules: List<ShipModule.Symbol>): Boolean {
        return ship
                .modules
                .map { it.symbol }
                .any { requiredModules.contains(it) }
    }

    suspend fun hasRequiredMounts(shipId: String, token: String, requiredMounts: List<ShipMount.Symbol>): Boolean {
        return getShip(token, shipId)
                .mounts
                .map { it.symbol }
                .any { requiredMounts.contains(it) }
    }

    suspend fun hasRequiredMounts(ship: Ship, requiredModules: List<ShipMount.Symbol>): Boolean {
        return ship.mounts
                .map { it.symbol }
                .any { requiredModules.contains(it) }
    }

    suspend fun isShipInOrbit(shipId: String, token: String): Boolean {
        return getShip(token, shipId)
                .nav
                .status == ShipNavStatus.IN_ORBIT
    }

    suspend fun isShipInOrbit(ship: Ship): Boolean {
        return ship.nav
                .status == ShipNavStatus.IN_ORBIT
    }

    suspend fun isShipInMarketplaceWaypoint(shipId: String, token: String): Boolean {
        val ship = getShip(token, shipId)
        with(ship) {
            val isDocked = this.nav.status == ShipNavStatus.DOCKED
            if (!isDocked) return false
        }
        val destination = ship.nav.route.destination
        val isWaypointIsMarketplace = systemsApi.getWaypoint(destination.systemSymbol, destination.symbol)
                .awaitSingle().data.traits.map { it.symbol }.contains(WaypointTraitSymbol.MARKETPLACE)
        return isWaypointIsMarketplace
    }

    private suspend fun getShip(token: String,
                                shipId: String): Ship {
        return fleetApi.getMyShip(token, shipId)
                .awaitSingle()
                .data
    }
}