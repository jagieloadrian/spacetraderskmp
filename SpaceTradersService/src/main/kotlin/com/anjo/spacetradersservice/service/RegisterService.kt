package com.anjo.spacetradersservice.service

import com.anjo.spacetradersservice.exception.NonSpaceTraderValidException
import com.anjo.spacetradersservice.generated.apis.DefaultApi
import com.anjo.spacetradersservice.generated.models.RegisterRequest
import com.anjo.spacetradersservice.generated.models.RegisterResponseData
import com.anjo.spacetradersservice.model.mapToUser
import com.anjo.spacetradersservice.repository.SpaceTraderRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RegisterService(
        private val defaultApi: DefaultApi,
        private val repository: SpaceTraderRepository,
) {

    suspend fun registerNewUser(request: RegisterRequest, nextResetDate: LocalDateTime): RegisterResponseData {
        if (nextResetDate.isBefore(LocalDateTime.now())) {
            throw NonSpaceTraderValidException("Non valid resetDate")
        } else {
            with(defaultApi.register(request)
                    .awaitSingle()
                    .data) {
                return this.also {
                    it.mapToUser(nextResetDate, request.email).also { user ->
                        repository.save(user)
                    }
                }
            }
        }
    }
}
