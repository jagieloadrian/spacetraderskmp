package com.anjo.spacetradersservice.testutils

import com.anjo.spacetradersservice.generated.infrastructure.RequestMethod
import org.mockserver.client.MockServerClient
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
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

fun mockExternalQueryWithAuth(mockServerClient: MockServerClient , token: String, requestMethod: RequestMethod, path: String, jsonPath: String) {
    mockServerClient.`when`(
            request()
                    .withHeader("Authorization", "Bearer $token")
                    .withMethod(requestMethod.name)
                    .withPath(path))
            .respond(response()
                    .withStatusCode(200)
                    .withHeaders(defaultHeader())
                    .withBody(readJson(jsonPath)))

}

fun mockExternalQuery(mockServerClient: MockServerClient , requestMethod: RequestMethod, path: String, jsonPath: String) {
    mockServerClient.`when`(
            request()
                    .withMethod(requestMethod.name)
                    .withPath(path))
            .respond(response()
                    .withStatusCode(200)
                    .withHeaders(defaultHeader())
                    .withBody(readJson(jsonPath)))

}