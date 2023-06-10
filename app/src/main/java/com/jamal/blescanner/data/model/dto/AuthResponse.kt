package com.jamal.blescanner.data.model.dto

import com.jamal.blescanner.data.model.User

data class AuthResponse(
    val tokens: TokensResponse,
    val user: User
)
