package com.ilkinbayramov.psst.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class User(
        val id: String,
        val email: String,
        /** The name other people see; chosen at sign-up and editable in the profile. */
        val displayName: String = "",
        val gender: String,
        val birthDate: String,
        val bio: String? = null,
        val profileImageUrl: String? = null,
        val isPremium: Boolean = false
)
