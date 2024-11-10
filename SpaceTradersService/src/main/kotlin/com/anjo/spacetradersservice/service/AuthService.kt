package com.anjo.spacetradersservice.service

import com.anjo.spacetradersservice.exception.NotFoundTokenException
import com.anjo.spacetradersservice.repository.SpaceTraderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class AuthService(private val repository: SpaceTraderRepository) {

    suspend fun findMyTokenByEmail(email: String): String {
        return withContext(Dispatchers.IO) {
            repository.getByIsActiveTrue()
        }.firstOrNull { it.email == email }?.token
            ?: throw NotFoundTokenException("Token for user with email: $email not found")
    }

    suspend fun findMyTokenByUserName(userName: String): String {
        return withContext(Dispatchers.IO) {
            repository.getByIsActiveTrue()
        }.firstOrNull { it.name == userName }?.token
            ?: throw NotFoundTokenException("Token for user with userName: $userName not found")
    }
}