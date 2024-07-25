package com.anjo.spacetradersservice.controller

import com.anjo.spacetradersservice.service.AuthService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @GetMapping("/email")
    suspend fun findMyTokenByEmail(@RequestParam email: String): String {
        return authService.findMyTokenByEmail(email)
    }

    @GetMapping("/username")
    suspend fun findMyTokenByUserName(@RequestParam userName: String): String {
        return authService.findMyTokenByUserName(userName.trim())
    }
}