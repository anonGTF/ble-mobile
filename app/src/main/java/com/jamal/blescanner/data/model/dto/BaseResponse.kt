package com.jamal.blescanner.data.model.dto

data class BaseResponse<T>(
    val content: T?,
    val error: ErrorResponse?
)
