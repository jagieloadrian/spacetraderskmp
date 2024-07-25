package com.anjo.spacetradersservice.testutils

import com.fasterxml.jackson.databind.ObjectMapper

object Mapper {
    private val objectMapper = ObjectMapper()

    fun writeAsString(value: Any) : String {
        return objectMapper.writeValueAsString(value)
    }
}