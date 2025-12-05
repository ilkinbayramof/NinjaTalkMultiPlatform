package com.ilkinbayramov.ninjatalk

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform