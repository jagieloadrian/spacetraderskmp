package com.anjo.spacetradersservice.controller

import com.anjo.spacetradersservice.generated.models.Faction
import com.anjo.spacetradersservice.generated.models.FactionsResponse
import com.anjo.spacetradersservice.service.FactionService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/factions")
class FactionsController(val factionService: FactionService) {

    @GetMapping
    suspend fun getAllFactions(@RequestParam(required = false) page: Int = 1): FactionsResponse {
        return factionService.getAllFactions(page)
    }

    @GetMapping("/{factionId}")
    suspend fun getFaction(
            @PathVariable factionId: String,
            @RequestHeader("token") token: String): Faction {
        return factionService.getFaction(factionId, token)
    }
}