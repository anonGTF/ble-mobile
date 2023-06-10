package com.jamal.blescanner.data.model.dto

import com.jamal.blescanner.data.model.Token

data class TokensResponse(
    val accessToken: Token,
    val refreshToken: Token
)
