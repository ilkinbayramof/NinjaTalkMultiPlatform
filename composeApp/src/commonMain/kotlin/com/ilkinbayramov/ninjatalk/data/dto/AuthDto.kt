package com.ilkinbayramov.ninjatalk.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
        val email: String,
        val password: String,
        val gender: String, // "MALE" or "FEMALE"
        val birthDate: String // "2000-01-15" format
)

@Serializable data class LoginRequest(val email: String, val password: String)

@Serializable data class AuthResponse(val token: String, val userId: String, val email: String)

@Serializable data class ErrorResponse(val error: String, val message: String)
