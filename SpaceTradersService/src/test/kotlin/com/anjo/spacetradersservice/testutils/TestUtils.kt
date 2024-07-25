package com.anjo.spacetradersservice.testutils

import org.mockserver.client.MockServerClient
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest.request
import org.mockserver.verify.VerificationTimes
import org.springframework.core.io.ClassPathResource

fun readJson(path: String): String {
    return ClassPathResource(path).inputStream.readAllBytes().toString(Charsets.UTF_8)
}

fun defaultHeader(): Header {
    return Header("Content-Type", "application/json; charset=utf-8")
}

fun MockServerClient.verifyMockServerRequest(method: String, path: String, times: Int) {
    this.verify(
            request().withMethod(method).withPath(path),
            VerificationTimes.exactly(times)
    )
}