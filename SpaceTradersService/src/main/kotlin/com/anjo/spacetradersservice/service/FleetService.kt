package com.anjo.spacetradersservice.service

import com.anjo.spacetradersservice.exception.NonValidActionException
import com.anjo.spacetradersservice.generated.apis.FleetApi
import com.anjo.spacetradersservice.generated.models.ChartShipResponseData
import com.anjo.spacetradersservice.generated.models.Contract
import com.anjo.spacetradersservice.generated.models.Cooldown
import com.anjo.spacetradersservice.generated.models.ExtractResourcesRequest
import com.anjo.spacetradersservice.generated.models.ExtractShipResponseData
import com.anjo.spacetradersservice.generated.models.InstallMountRequest
import com.anjo.spacetradersservice.generated.models.InstallMountResponseData
import com.anjo.spacetradersservice.generated.models.JettisonRequest
import com.anjo.spacetradersservice.generated.models.JumpShipRequest
import com.anjo.spacetradersservice.generated.models.JumpShipResponseData
import com.anjo.spacetradersservice.generated.models.NavigateShipRequest
import com.anjo.spacetradersservice.generated.models.NavigateShipResponseData
import com.anjo.spacetradersservice.generated.models.PatchShipNavRequest
import com.anjo.spacetradersservice.generated.models.PurchaseCargoRequest
import com.anjo.spacetradersservice.generated.models.PurchaseShipRequest
import com.anjo.spacetradersservice.generated.models.RefuelShipRequest
import com.anjo.spacetradersservice.generated.models.RefuelShipResponseData
import com.anjo.spacetradersservice.generated.models.RemoveMountRequest
import com.anjo.spacetradersservice.generated.models.RemoveMountResponseData
import com.anjo.spacetradersservice.generated.models.RepairShipResponseData
import com.anjo.spacetradersservice.generated.models.RepairTransaction
import com.anjo.spacetradersservice.generated.models.ScanShipResponseData
import com.anjo.spacetradersservice.generated.models.ScrapShipResponseData
import com.anjo.spacetradersservice.generated.models.ScrapTransaction
import com.anjo.spacetradersservice.generated.models.SellCargoRequest
import com.anjo.spacetradersservice.generated.models.SellShipResponseData
import com.anjo.spacetradersservice.generated.models.Ship
import com.anjo.spacetradersservice.generated.models.ShipCargo
import com.anjo.spacetradersservice.generated.models.ShipMount
import com.anjo.spacetradersservice.generated.models.ShipNav
import com.anjo.spacetradersservice.generated.models.ShipPurchaseResponseData
import com.anjo.spacetradersservice.generated.models.ShipRefineRequest
import com.anjo.spacetradersservice.generated.models.ShipRefineResponseData
import com.anjo.spacetradersservice.generated.models.ShipsResponse
import com.anjo.spacetradersservice.generated.models.SiphonShipResponseData
import com.anjo.spacetradersservice.generated.models.SurveyShipResponseData
import com.anjo.spacetradersservice.generated.models.SystemScanShipResponseData
import com.anjo.spacetradersservice.generated.models.TransferCargoRequest
import com.anjo.spacetradersservice.generated.models.WarpShipResponseData
import com.anjo.spacetradersservice.generated.models.WaypointScanShipResponseData
import com.anjo.spacetradersservice.utils.Constants.EXTRACT_MOUNTS
import com.anjo.spacetradersservice.utils.Constants.GAS_SIPHON_MODULES
import com.anjo.spacetradersservice.utils.Constants.GAS_SIPHON_MOUNTS
import com.anjo.spacetradersservice.utils.Constants.REFINERY_MODULES
import com.anjo.spacetradersservice.utils.Constants.SENSOR_MOUNTS
import com.anjo.spacetradersservice.utils.Constants.SURVEYOR_MOUNTS
import com.anjo.spacetradersservice.utils.Constants.WARP_MODULES
import com.anjo.spacetradersservice.utils.prepareToken
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service

@Service
class FleetService(
        private val fleetApi: FleetApi,
        private val shipActionValidatorService: ShipActionValidatorService,
) {

    suspend fun getMyShips(page: Int, token: String): ShipsResponse {
        return fleetApi.getMyShips(prepareToken(token), page)
                .awaitSingle()
    }

    suspend fun getMyShip(shipId: String, token: String): Ship {
        return fleetApi.getMyShip(prepareToken(token), shipId)
                .awaitSingle()
                .data
    }

    suspend fun purchaseShip(purchaseShipRequest: PurchaseShipRequest, token: String): ShipPurchaseResponseData {
        return fleetApi.purchaseShip(prepareToken(token), purchaseShipRequest)
                .awaitSingle()
                .data
    }

    suspend fun orbitShip(shipId: String, token: String): ShipNav {
        return fleetApi.orbitShip(shipId, prepareToken(token))
                .awaitSingle()
                .data
                .nav
    }

    suspend fun refineOnShip(
            shipId: String, token: String,
            shipRefineRequest: ShipRefineRequest,
    ): ShipRefineResponseData {
        val prepareToken = prepareToken(token)
        val requiredMounts = shipActionValidatorService.hasRequiredModule(shipId, prepareToken, REFINERY_MODULES)
        return when (requiredMounts) {
            true  -> fleetApi.shipRefine(shipId, prepareToken, shipRefineRequest)
                    .awaitSingle()
                    .data

            false -> throw NonValidActionException("This ship does not have required modules")
        }
    }

    suspend fun createChart(shipId: String, token: String): ChartShipResponseData {
        return fleetApi.createChart(shipId, prepareToken(token))
                .awaitSingle()
                .data
    }

    suspend fun getShipCooldown(shipId: String, token: String): Cooldown {
        //todo przetestować działanie multistatusów: 200 albo 204
        return fleetApi.getShipCooldown(shipId, prepareToken(token))
                .awaitSingle()
                .data
    }

    suspend fun dockShip(shipId: String, token: String): ShipNav {
        return fleetApi.dockShip(shipId, prepareToken(token))
                .awaitSingle()
                .data
                .nav
    }

    suspend fun createSurvey(shipId: String, token: String): SurveyShipResponseData {
        return when (shipActionValidatorService.hasRequiredMounts(shipId, prepareToken(token), SURVEYOR_MOUNTS)) {
            true  -> fleetApi.createSurvey(shipId, prepareToken(token))
                    .awaitSingle().data

            false -> throw NonValidActionException("This ship does not have required mounts")
        }
    }

    suspend fun extractResource(shipId: String, token: String): ExtractShipResponseData {
        val ship = getMyShip(shipId, token)
        return when (
            shipActionValidatorService.isShipInOrbit(ship) &&
                    shipActionValidatorService.hasRequiredMounts(ship, EXTRACT_MOUNTS)) {
            true  -> fleetApi.extractResources(shipId, prepareToken(token), ExtractResourcesRequest(null))
                    .awaitSingle()
                    .data

            false -> throw NonValidActionException("This ship does not have required mounts")
        }
    }

    suspend fun extractGasResources(shipId: String, token: String): SiphonShipResponseData {
        val ship = getMyShip(shipId, token)
        return when (shipActionValidatorService.isShipInOrbit(ship)
                && shipActionValidatorService.hasRequiredModule(ship, GAS_SIPHON_MODULES)
                && shipActionValidatorService.hasRequiredMounts(ship, GAS_SIPHON_MOUNTS)) {
            false -> throw NonValidActionException("This ship does not have required mounts/modules")
            true  -> fleetApi.siphonResources(shipId, prepareToken(token))
                    .awaitSingle()
                    .data
        }
    }

    suspend fun extractResourceFromWaypoint(
            shipId: String, token: String,
            request: ExtractResourcesRequest,
    ): ExtractShipResponseData {
        //todo sprawdzić jak można to potem rozróżnić
//        val depositType = request.survey
//                ?.deposits
//                ?.takeIf { it.isNotEmpty() }
//                ?.distinctBy { it.symbol }
//                ?.takeIf { it.size == 1 }
//                ?.map { it.symbol }
//                ?.first() ?: throw NonValidActionException("There's more than one accepted type resource")
        val ship = getMyShip(shipId, token)
        return when (shipActionValidatorService.hasRequiredMounts(ship, EXTRACT_MOUNTS)) {
            true  -> request.survey?.let {
                fleetApi.extractResourcesWithSurvey(shipId, prepareToken(token), it)
                        .awaitSingle()
                        .data
            } ?: throw NonValidActionException("This endpoint required properly request body")

            false -> throw NonValidActionException("This ship does not have required mounts")
        }
    }

    suspend fun throwAwayResources(shipId: String, token: String, jettisonRequest: JettisonRequest): ShipCargo {
        return fleetApi.jettison(shipId, prepareToken(token), jettisonRequest)
                .awaitSingle()
                .data
                .cargo
    }

    suspend fun jumpByShip(shipId: String, token: String, jumpShipRequest: JumpShipRequest): JumpShipResponseData {
        return when (shipActionValidatorService.isShipInOrbit(shipId, prepareToken(token))) {
            true  -> fleetApi.jumpShip(shipId, prepareToken(token), jumpShipRequest)
                    .awaitSingle()
                    .data

            false -> throw NonValidActionException("This ship isn't in orbit state")
        }
    }

    suspend fun navigateShip(shipId: String, token: String, request: NavigateShipRequest): NavigateShipResponseData {
        return fleetApi.navigateShip(shipId, prepareToken(token), request)
                .awaitSingle()
                .data
    }

    suspend fun changeFlightMode(shipId: String, token: String, request: PatchShipNavRequest): ShipNav {
        return fleetApi.patchShipNav(shipId, prepareToken(token), request)
                .awaitSingle()
                .data
    }

    suspend fun getNavShipStatus(shipId: String, token: String): ShipNav {
        return fleetApi.getShipNav(shipId, prepareToken(token))
                .awaitSingle()
                .data
    }

    suspend fun warpShip(shipId: String, token: String, request: NavigateShipRequest): WarpShipResponseData {
        return when (shipActionValidatorService.hasRequiredModule(shipId, prepareToken(token), WARP_MODULES)) {
            true  -> fleetApi.warpShip(shipId, prepareToken(token), request)
                    .awaitSingle()
                    .data

            false -> throw NonValidActionException("This ship does not have required modules")
        }
    }

    suspend fun sellCargo(shipId: String, token: String, request: SellCargoRequest): SellShipResponseData {
        return when (shipActionValidatorService.isShipInMarketplaceWaypoint(shipId, prepareToken(token))) {
            true  -> fleetApi.sellCargo(shipId, prepareToken(token), request)
                    .awaitSingle()
                    .data

            false -> throw NonValidActionException("This ship is not in marketplace waypoint")
        }
    }

    suspend fun scanSystems(shipId: String, token: String): SystemScanShipResponseData {
        return when (shipActionValidatorService.hasRequiredMounts(shipId, prepareToken(token), SENSOR_MOUNTS)) {
            true  -> fleetApi.createShipSystemScan(shipId, prepareToken(token))
                    .awaitSingle()
                    .data

            false -> throw NonValidActionException("This ship does not have required mounts")
        }
    }

    suspend fun scanSystemsWaypoints(shipId: String, token: String): WaypointScanShipResponseData {
        return when (shipActionValidatorService.hasRequiredMounts(shipId, prepareToken(token), SENSOR_MOUNTS)) {
            true  -> fleetApi.createShipWaypointScan(shipId, prepareToken(token))
                    .awaitSingle()
                    .data

            false -> throw NonValidActionException("This ship does not have required mounts")
        }
    }

    suspend fun scanShips(shipId: String, token: String): ScanShipResponseData {
        return when (shipActionValidatorService.hasRequiredMounts(shipId, prepareToken(token), SENSOR_MOUNTS)) {
            true  -> fleetApi.createShipShipScan(shipId, prepareToken(token))
                    .awaitSingle()
                    .data

            false -> throw NonValidActionException("This ship does not have required mounts")
        }
    }

    suspend fun refuelShip(shipId: String, token: String, refuelShipRequest: RefuelShipRequest): RefuelShipResponseData {
        return fleetApi.refuelShip(shipId, prepareToken(token), refuelShipRequest)
                .awaitSingle()
                .data
    }

    suspend fun purchaseCargo(shipId: String, token: String, purchaseCargoRequest: PurchaseCargoRequest): SellShipResponseData {
        return fleetApi.purchaseCargo(shipId, prepareToken(token), purchaseCargoRequest)
                .awaitSingle()
                .data
    }

    suspend fun transferCargo(shipId: String, token: String, transferCargoRequest: TransferCargoRequest): ShipCargo {
        return  fleetApi.transferCargo(shipId, prepareToken(token), transferCargoRequest)
                .awaitSingle()
                .data
                .cargo
    }

    suspend fun negotiateContract(shipId: String, token: String): Contract {
        return fleetApi.negotiateContract(shipId, prepareToken(token))
                .awaitSingle()
                .data
                .contract
    }

    suspend fun getMounts(shipId: String, token: String): List<ShipMount> {
        return fleetApi.getMounts(shipId, prepareToken(token))
                .awaitSingle()
                .data
    }

    suspend fun installMount(shipId: String, token: String, installMountRequest: InstallMountRequest): InstallMountResponseData {
        return fleetApi.installMount(shipId, prepareToken(token), installMountRequest)
                .awaitSingle()
                .data
    }

    suspend fun removeMount(shipId: String, token: String, removeMountRequest: RemoveMountRequest): RemoveMountResponseData {
        return fleetApi.removeMount(shipId, prepareToken(token), removeMountRequest)
                .awaitSingle()
                .data
    }

    suspend fun getPossibleValueFromScrapShip(shipId: String, token: String): ScrapTransaction {
        return fleetApi.getScrapShip(shipId, prepareToken(token))
                .awaitSingle()
                .data
                .transaction
    }

    suspend fun scrapShip(shipId: String, token: String): ScrapShipResponseData {
        return fleetApi.scrapShip(shipId, prepareToken(token))
                .awaitSingle()
                .data
    }

    suspend fun getCostOfRepairShip(shipId: String, token: String): RepairTransaction {
        return fleetApi.getRepairShip(shipId, prepareToken(token))
                .awaitSingle()
                .data
                .transaction
    }

    suspend fun repairShip(shipId: String, token: String): RepairShipResponseData {
        return fleetApi.repairShip(shipId, prepareToken(token))
                .awaitSingle()
                .data
    }
}
