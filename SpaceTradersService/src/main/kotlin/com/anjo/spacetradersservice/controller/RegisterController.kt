package com.anjo.spacetradersservice.controller

import com.anjo.spacetradersservice.generated.models.RegisterRequest
import com.anjo.spacetradersservice.generated.models.RegisterResponseData
import com.anjo.spacetradersservice.service.RegisterService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/register")
class RegisterController(private val registerService: RegisterService) {

    @PostMapping
    suspend fun register(
            @RequestBody request: RegisterRequest,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam nextResetDate: LocalDateTime): RegisterResponseData {
        return registerService.registerNewUser(request, nextResetDate)
    }
}