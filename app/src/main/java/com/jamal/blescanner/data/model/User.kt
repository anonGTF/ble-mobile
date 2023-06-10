package com.jamal.blescanner.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val email: String,
    val name: String,
    val nip: Int,
    val role: String,
    @SerializedName("is_verified")
    val isVerified: Boolean
)
