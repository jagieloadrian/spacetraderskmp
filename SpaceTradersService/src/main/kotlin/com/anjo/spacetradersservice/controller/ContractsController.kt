package com.anjo.spacetradersservice.controller

import com.anjo.spacetradersservice.generated.models.Contract
import com.anjo.spacetradersservice.generated.models.ContractsResponse
import com.anjo.spacetradersservice.generated.models.DeliverContractRequest
import com.anjo.spacetradersservice.service.ContractsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/contracts")
class ContractsController(val contractsService: ContractsService) {

    @GetMapping
    suspend fun getMyContracts(
            @RequestHeader("token") token: String,
            @RequestParam(required = false) page: Int = 1): ContractsResponse {
        return contractsService.getMyContracts(token, page)
    }

    @GetMapping("/{contractId}")
    suspend fun getMyContract(
            @PathVariable contractId: String,
            @RequestHeader("token") token: String): Contract {
        return contractsService.getMyContract(contractId, token)
    }

    @PostMapping("/{contractId}/accept")
    suspend fun acceptNewContract(
            @PathVariable contractId: String,
            @RequestHeader("token") token: String): Contract {
        return contractsService.acceptNewContract(contractId, token)
    }

    @PostMapping("/{contractId}/deliver-cargo")
    suspend fun deliverCargo(
            @PathVariable contractId: String,
            @RequestHeader("token") token: String,
            @RequestBody deliverRequest: DeliverContractRequest): Contract {
        return contractsService.deliverCargoForContract(contractId, token, deliverRequest)
    }
}