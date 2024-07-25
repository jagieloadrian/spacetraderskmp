package com.anjo.spacetradersservice.configuration

import com.anjo.spacetradersservice.generated.apis.AgentsApi
import com.anjo.spacetradersservice.generated.apis.DefaultApi
import com.anjo.spacetradersservice.generated.apis.SystemsApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ApiClientConfig(
        @Value("\${app.baseUrl}") val baseUrl: String,
) {

    @Bean
    fun getAgentsApi(): AgentsApi {
        return AgentsApi(baseUrl)
    }

    @Bean
    fun getSystemApi(): SystemsApi {
        return SystemsApi(baseUrl)
    }

    @Bean
    fun getDefaultApi(): DefaultApi {
        return DefaultApi(baseUrl)
    }
}