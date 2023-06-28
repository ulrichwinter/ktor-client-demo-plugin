package com.example

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.statuspages.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(SimplePlugin)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "There was an error:  ${cause.message}", status = HttpStatusCode.BadRequest)
        }
    }
    routing {
        install(Check1Plugin)
        install(Check2Plugin)
        get("/") {
            call.respondText("Hello, world!")
        }
        get("/error") {
            throw RuntimeException("Test exception")
        }
    }
}

val SimplePlugin = createApplicationPlugin(name = "SimplePlugin") {
    onCall { call ->
        println("SimplePlugin: Call ${call.request.uri} started")
    }
    on(CallFailed) { call, cause ->
        println("SimplePlugin: Call ${call.request.uri} failed ${cause.message}")
    }
    on(ResponseBodyReadyForSend) { call, content ->
        val responseContent = when (content) {
            is TextContent -> {
                content.text
            }

            else -> {
                ""
            }
        }
        println("SimplePlugin: Call ${call.request.uri} finished with status ${content.status} and content $responseContent")
    }
}

val Check1Plugin: RouteScopedPlugin<Unit> = createRouteScopedPlugin("Check1Plugin") {
    onCall { call ->
        println("Check1Plugin: Call ${call.request.uri} started")
    }
    on(CallFailed) { call, cause ->
        println("Check1Plugin: Call ${call.request.uri} failed ${cause.message}")
    }
    on(ResponseBodyReadyForSend) { call, response ->
        println("Check1Plugin: Call ${call.request.uri} finished with status ${response.status}")
    }
}

val Check2Plugin: RouteScopedPlugin<Unit> = createRouteScopedPlugin("Check2Plugin") {
    onCall { call ->
        println("Check2Plugin: Call ${call.request.uri} started")
    }
    on(CallFailed) { call, cause ->
        println("Check2Plugin: Call ${call.request.uri} failed ${cause.message}")
    }
    on(ResponseBodyReadyForSend) { call, response ->
        println("Check2Plugin: Call ${call.request.uri} finished with status ${response.status}")
    }
}
