package com.abelgas.mobile.api

import com.abelgas.mobile.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<Map<String, Any>>>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginData>>

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Any>>

    @GET("user/me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ApiResponse<User>>
}