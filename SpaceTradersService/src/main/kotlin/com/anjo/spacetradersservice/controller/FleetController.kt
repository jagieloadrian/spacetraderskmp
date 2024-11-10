package com.anjo.spacetradersservice.controller

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
import com.anjo.spacetradersservice.service.FleetService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/fleet")
class FleetController(val fleetService: FleetService) {

    @GetMapping
    suspend fun getMyShips(
            @RequestHeader("token") token: String,
            @RequestParam(required = false) page: Int = 1): ShipsResponse {
        return fleetService.getMyShips(page, token)
    }

    @GetMapping("/{shipId}")
    suspend fun getMyShip(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String): Ship {
        return fleetService.getMyShip(shipId, token)
    }

    @PostMapping
    suspend fun purchaseShip(
            @RequestHeader("token") token: String,
            @RequestBody request: PurchaseShipRequest): ShipPurchaseResponseData {
        return fleetService.purchaseShip(request, token)
    }

    @PostMapping("/{shipId}/orbit")
    suspend fun orbitMyShip(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String): ShipNav {
        return fleetService.orbitShip(shipId, token)
    }

    @PostMapping("/{shipId}/refine-on-ship")
    suspend fun refineOnShip(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String,
            @RequestBody request: ShipRefineRequest): ShipRefineResponseData {
        return fleetService.refineOnShip(shipId, token, request)
    }


    @PostMapping("/{shipId}/create-chart")
    suspend fun createChart(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String): ChartShipResponseData {
        return fleetService.createChart(shipId, token)
    }

    @GetMapping("/{shipId}/cooldown")
    suspend fun cooldown(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String): Cooldown {
        return fleetService.getShipCooldown(shipId, token)
    }

    @PostMapping("/{shipId}/dock")
    suspend fun dockShip(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String): ShipNav {
        return fleetService.dockShip(shipId, token)
    }

    @PostMapping("/{shipId}/create-survey")
    suspend fun createSurvey(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String): SurveyShipResponseData {
        return fleetService.createSurvey(shipId, token)
    }

    @PostMapping("/{shipId}/extract-resource")
    suspend fun extractResource(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String): ExtractShipResponseData {
        return fleetService.extractResource(shipId, token)
    }

    @PostMapping("/{shipId}/extract-gas")
    suspend fun extractGas(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String): SiphonShipResponseData {
        return fleetService.extractGasResources(shipId, token)
    }

    @PostMapping("/{shipId}/extract-from-waypoint")
    suspend fun extractFromWaypoint(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String,
            @RequestBody request: ExtractResourcesRequest): ExtractShipResponseData {
        return fleetService.extractResourceFromWaypoint(shipId, token, request)
    }

    @PostMapping("/{shipId}/jettison-resources")
    suspend fun throwAwayResources(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String,
            @RequestBody request: JettisonRequest): ShipCargo {
        return fleetService.throwAwayResources(shipId, token, request)
    }

    @PostMapping("/{shipId}/jump")
    suspend fun jumpByShip(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String,
            @RequestBody request: JumpShipRequest): JumpShipResponseData {
        return fleetService.jumpByShip(shipId, token, request)
    }

    @PostMapping("/{shipId}/navigate")
    suspend fun navigate(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String,
            @RequestBody request: NavigateShipRequest): NavigateShipResponseData {
        return fleetService.navigateShip(shipId, token, request)
    }

    @PatchMapping("/{shipId}/flight-mode")
    suspend fun changeFlightMode(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String,
            @RequestBody request: PatchShipNavRequest): ShipNav {
        return fleetService.changeFlightMode(shipId, token, request)
    }

    @GetMapping("/{shipId}/navigation")
    suspend fun getNavigation(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String): ShipNav {
        return fleetService.getNavShipStatus(shipId, token)
    }

    @PostMapping("/{shipId}/warp")
    suspend fun warpShip(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String,
            @RequestBody request: NavigateShipRequest): WarpShipResponseData {
        return fleetService.warpShip(shipId, token, request)
    }

    @PostMapping("/{shipId}/sell-cargo")
    suspend fun sellCargo(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String,
            @RequestBody request: SellCargoRequest): SellShipResponseData {
        return fleetService.sellCargo(shipId, token, request)
    }

    @PostMapping("/{shipId}/scan-system")
    suspend fun scanSystem(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String): SystemScanShipResponseData {
        return fleetService.scanSystems(shipId, token)
    }

    @PostMapping("/{shipId}/scan-waypoints")
    suspend fun scanWaypoints(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String): WaypointScanShipResponseData {
        return fleetService.scanSystemsWaypoints(shipId, token)
    }

    @PostMapping("/{shipId}/scan-ships")
    suspend fun scanShips(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String): ScanShipResponseData {
        return fleetService.scanShips(shipId, token)
    }

    @PostMapping("/{shipId}/refuel-ship")
    suspend fun refuelShip(
            @PathVariable shipId: String,
            @RequestHeader("token") token: String,
            @RequestBody request: RefuelShipRequest): RefuelShipResponseData {
        return fleetService.refuelShip(shipId, token, request)
    }

    @PostMapping("/{shipId}/purchase-cargo")
    suspend fun purchaseCargo(@PathVariable shipId: String,
                              @RequestHeader("token") token: String,
                              @RequestBody request: PurchaseCargoRequest): SellShipResponseData {
        return fleetService.purchaseCargo(shipId, token, request)
    }

    @PostMapping("/{shipId}/transfer-cargo")
    suspend fun transferCargo(@PathVariable shipId: String,
                              @RequestHeader("token") token: String,
                              @RequestBody request: TransferCargoRequest): ShipCargo {
        return fleetService.transferCargo(shipId, token, request)
    }

    @PostMapping("/{shipId}/negotiate-contract")
    suspend fun negotiateContract(@PathVariable shipId: String,
                                  @RequestHeader("token") token: String): Contract {
        return fleetService.negotiateContract(shipId, token)
    }

    @GetMapping("/{shipId}/mount")
    suspend fun getShipMounts(@PathVariable shipId: String,
                              @RequestHeader("token") token: String): List<ShipMount> {
        return fleetService.getMounts(shipId, token)
    }

    @PostMapping("/{shipId}/mount")
    suspend fun installMount(@PathVariable shipId: String,
                             @RequestHeader("token") token: String,
                             @RequestBody request: InstallMountRequest): InstallMountResponseData {
        return fleetService.installMount(shipId, token, request)
    }

    @DeleteMapping("/{shipId}/mount")
    suspend fun removeMount(@PathVariable shipId: String,
                            @RequestHeader("token") token: String,
                            @RequestBody request: RemoveMountRequest): RemoveMountResponseData {
        return fleetService.removeMount(shipId, token, request)
    }

    @GetMapping("/{shipId}/scrap")
    suspend fun getPossibleValueFromScrapShip(@PathVariable shipId: String,
                                              @RequestHeader("token") token: String): ScrapTransaction {
        return fleetService.getPossibleValueFromScrapShip(shipId, token)
    }

    @PostMapping("/{shipId}/scrap")
    suspend fun scrapShip(@PathVariable shipId: String,
                          @RequestHeader("token") token: String): ScrapShipResponseData {
        return fleetService.scrapShip(shipId, token)
    }

    @GetMapping("/{shipId}/repair")
    suspend fun getCostOfRepair(@PathVariable shipId: String,
                                @RequestHeader("token") token: String): RepairTransaction {
        return fleetService.getCostOfRepairShip(shipId, token)
    }

    @PostMapping("/{shipId}/repair")
    suspend fun repairShip(@PathVariable shipId: String,
                           @RequestHeader("token") token: String): RepairShipResponseData {
        return fleetService.repairShip(shipId, token)
    }
}