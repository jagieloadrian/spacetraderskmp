package com.anjo.spacetradersservice.service

import com.anjo.spacetradersservice.generated.apis.ContractsApi
import com.anjo.spacetradersservice.generated.models.Contract
import com.anjo.spacetradersservice.generated.models.ContractsResponse
import com.anjo.spacetradersservice.generated.models.DeliverContractRequest
import com.anjo.spacetradersservice.utils.prepareToken
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service

@Service
class ContractsService(private val contractsApi: ContractsApi) {

    suspend fun getMyContracts(token: String, page: Int): ContractsResponse {
        return contractsApi.getContracts(prepareToken(token), page)
                .awaitSingle()
    }

    suspend fun getMyContract(contractId: String, token: String): Contract {
        return contractsApi.getContract(prepareToken(token), contractId)
                .awaitSingle()
                .data
    }

    suspend fun acceptNewContract(contractId: String, token: String): Contract {
        return contractsApi.acceptContract(contractId, prepareToken(token))
                .awaitSingle()
                .data
                .contract
    }

    suspend fun deliverCargoForContract(
            contractId: String, token: String,
            deliverRequest: DeliverContractRequest): Contract {
        return contractsApi.deliverContract(contractId, prepareToken(token = token), deliverRequest)
                .awaitFirst()
                .data
                .contract
                .apply {
                    if (shouldFulfilled(this)) {
                        return contractsApi.fulfillContract(contractId, prepareToken(token = token))
                                .awaitSingle()
                                .data
                                .contract
                    }
                }
    }

    private fun shouldFulfilled(contract: Contract): Boolean {
        return contract.terms.deliver.takeIf { !it.isNullOrEmpty() }
                ?.all { deliver -> deliver.unitsFulfilled >= deliver.unitsRequired }
            ?: false
    }
}
