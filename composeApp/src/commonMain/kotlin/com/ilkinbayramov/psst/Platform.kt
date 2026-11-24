package com.ilkinbayramov.psst

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform