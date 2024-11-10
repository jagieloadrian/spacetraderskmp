package com.anjo.spacetradersservice.utils

import com.anjo.spacetradersservice.generated.models.ShipModule.Symbol.FUEL_REFINERY_I
import com.anjo.spacetradersservice.generated.models.ShipModule.Symbol.GAS_PROCESSOR_I
import com.anjo.spacetradersservice.generated.models.ShipModule.Symbol.MICRO_REFINERY_I
import com.anjo.spacetradersservice.generated.models.ShipModule.Symbol.ORE_REFINERY_I
import com.anjo.spacetradersservice.generated.models.ShipModule.Symbol.WARP_DRIVE_I
import com.anjo.spacetradersservice.generated.models.ShipModule.Symbol.WARP_DRIVE_II
import com.anjo.spacetradersservice.generated.models.ShipModule.Symbol.WARP_DRIVE_III
import com.anjo.spacetradersservice.generated.models.ShipMount.Symbol.GAS_SIPHON_I
import com.anjo.spacetradersservice.generated.models.ShipMount.Symbol.GAS_SIPHON_II
import com.anjo.spacetradersservice.generated.models.ShipMount.Symbol.GAS_SIPHON_III
import com.anjo.spacetradersservice.generated.models.ShipMount.Symbol.MINING_LASER_I
import com.anjo.spacetradersservice.generated.models.ShipMount.Symbol.MINING_LASER_II
import com.anjo.spacetradersservice.generated.models.ShipMount.Symbol.MINING_LASER_III
import com.anjo.spacetradersservice.generated.models.ShipMount.Symbol.SENSOR_ARRAY_I
import com.anjo.spacetradersservice.generated.models.ShipMount.Symbol.SENSOR_ARRAY_II
import com.anjo.spacetradersservice.generated.models.ShipMount.Symbol.SENSOR_ARRAY_III
import com.anjo.spacetradersservice.generated.models.ShipMount.Symbol.SURVEYOR_I
import com.anjo.spacetradersservice.generated.models.ShipMount.Symbol.SURVEYOR_II
import com.anjo.spacetradersservice.generated.models.ShipMount.Symbol.SURVEYOR_III

object Constants {
    const val BEARER_PREFIX = "Bearer"

    //sprawdzić czy w ten sposób nie będzie wywalać błędów
    val REFINERY_MODULES = listOf(MICRO_REFINERY_I, ORE_REFINERY_I, FUEL_REFINERY_I)
    val SURVEYOR_MOUNTS = listOf(SURVEYOR_I, SURVEYOR_II, SURVEYOR_III)
    val GAS_SIPHON_MOUNTS = listOf(GAS_SIPHON_I, GAS_SIPHON_II, GAS_SIPHON_III)
    val GAS_SIPHON_MODULES = listOf(GAS_PROCESSOR_I)
    val EXTRACT_MOUNTS =
        listOf(GAS_SIPHON_I, GAS_SIPHON_II, GAS_SIPHON_III, MINING_LASER_I, MINING_LASER_II, MINING_LASER_III)
    val WARP_MODULES = listOf(WARP_DRIVE_I, WARP_DRIVE_II, WARP_DRIVE_III)
    val SENSOR_MOUNTS = listOf(SENSOR_ARRAY_I, SENSOR_ARRAY_II, SENSOR_ARRAY_III)
}