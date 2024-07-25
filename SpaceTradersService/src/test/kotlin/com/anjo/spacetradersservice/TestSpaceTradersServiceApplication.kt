package com.anjo.spacetradersservice

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<SpaceTradersServiceApplication>()
            .with(TestcontainersConfiguration::class)
            .run(*args)
}
