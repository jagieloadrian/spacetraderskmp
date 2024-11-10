package com.anjo.spacetradersservice.testutils

import com.fasterxml.jackson.databind.json.JsonMapper

object Mapper {
    private val objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .build()

    fun writeAsString(value: Any) : String {
        return objectMapper.writeValueAsString(value)
    }
}