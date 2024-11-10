package com.anjo.spacetradersservice.model

import com.anjo.spacetradersservice.generated.models.RegisterResponseData
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.UUID

@Entity
data class SpaceTraderUser(
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        val id: UUID = UUID.randomUUID(),
        @Column(nullable = false)
        val name: String,
        @Column(nullable = false)
        val email: String,
        @Column(nullable = false, columnDefinition = "TEXT", length = 2048)
        val token: String,
        @Column(nullable = false)
        val resetDate: LocalDateTime,
        @Column(nullable = false)
        val isActive: Boolean,
)

fun RegisterResponseData.mapToUser(resetDate: LocalDateTime, userEmail: String?): SpaceTraderUser {
    return SpaceTraderUser(
            name = this.agent.symbol,
            email = userEmail ?: "",
            token = token,
            resetDate = resetDate,
            isActive = resetDate.isAfter(LocalDateTime.now())
    )
}