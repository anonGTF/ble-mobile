package com.jamal.blescanner.data.remote

import com.jamal.blescanner.data.model.dto.AuthResponse
import com.jamal.blescanner.data.model.dto.BaseResponse
import com.jamal.blescanner.data.model.dto.DeleteResponse
import com.jamal.blescanner.data.model.dto.DeviceResponse
import com.jamal.blescanner.data.model.dto.DevicesResponse
import com.jamal.blescanner.data.model.dto.LogoutResponse
import com.jamal.blescanner.data.model.dto.UserResponse
import retrofit2.http.*

interface BaseApi {

    // Auth

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
            @Field(value = "email", encoded = true) email: String,
            @Field("password") password: String
    ): BaseResponse<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(): BaseResponse<LogoutResponse>

    @GET("private/user")
    suspend fun getProfile(
        @Query("id") id: Int
    ): BaseResponse<UserResponse>

    /// Device

    @GET("private/devices")
    suspend fun getDevices(): BaseResponse<DevicesResponse>

    @FormUrlEncoded
    @POST("private/device/add")
    suspend fun addDevice(
        @Field("userId") userId: Int,
        @Field("name") name: String,
        @Field("uuid") uuid: String,
        @Field("mac") mac: String,
        @Field("major") major: Int,
        @Field("minor") minor: Int,
        @Field("rackNo") rackNo: Int,
        @Field("password") password: String
    ): BaseResponse<DeviceResponse>

    @FormUrlEncoded
    @POST("private/device/update")
    suspend fun updateDevice(
        @Field("id") deviceId: Int,
        @Field("name") name: String,
        @Field("uuid") uuid: String,
        @Field("mac") mac: String,
        @Field("major") major: Int,
        @Field("minor") minor: Int,
        @Field("rackNo") rackNo: Int,
        @Field("password") password: String
    ): BaseResponse<DeviceResponse>

    @FormUrlEncoded
    @POST("private/device/remove")
    suspend fun deleteDevice(
        @Field("id") id: Int,
        @Field("userId") userId: Int
    ): BaseResponse<DeleteResponse>
}