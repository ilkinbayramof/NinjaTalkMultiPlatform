package com.ilkinbayramov.ninjatalk.websocket

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*

actual fun createHttpClient(): HttpClient = HttpClient(CIO) {
    install(WebSockets)
}
