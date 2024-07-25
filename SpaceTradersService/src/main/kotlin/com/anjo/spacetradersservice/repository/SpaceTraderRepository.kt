package com.anjo.spacetradersservice.repository

import com.anjo.spacetradersservice.model.SpaceTraderUser
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface SpaceTraderRepository : CrudRepository<SpaceTraderUser, UUID> {

    fun getByIsActiveTrue(): List<SpaceTraderUser>
}