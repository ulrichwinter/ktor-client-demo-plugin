package com.example

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*

/**
 * This ktor client plugin reads the response body but keeps it available for the application code using the client (and for other plugins).
 * This is achieved by using the ktor internal ResponseObserver class.
 */
public class ResponseReading private constructor(
) {


    public companion object : HttpClientPlugin<Config, ResponseReading> {
        override val key: AttributeKey<ResponseReading> = AttributeKey("ResponseReading")

        override fun prepare(block: Config.() -> Unit): ResponseReading {
            val config = Config().apply(block)
            return ResponseReading()
        }

        override fun install(plugin: ResponseReading, scope: HttpClient) {
            plugin.setupReadResponse(scope)
        }
    }

    private fun setupReadResponse(scope: HttpClient) {
        val observer: ResponseHandler = observer@{ response ->
            val body = String(response.readBytes(), response.charset() ?: Charsets.UTF_8)
            println("body as read in plugin: $body")
        }
        ResponseObserver.install(ResponseObserver(observer), scope)
    }

    public class Config {
    }
}
