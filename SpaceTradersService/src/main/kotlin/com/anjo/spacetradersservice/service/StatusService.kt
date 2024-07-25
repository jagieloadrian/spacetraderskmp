package com.anjo.spacetradersservice.service

import com.anjo.spacetradersservice.generated.apis.DefaultApi
import com.anjo.spacetradersservice.generated.models.DefaultSystemResponse
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service

@Service
class StatusService(private val defaultApi: DefaultApi) {

    suspend fun returnSystemInfo(): DefaultSystemResponse {
        with(defaultApi.getStatus().awaitSingle()) {
            println(this)
            return this
        }
    }
}