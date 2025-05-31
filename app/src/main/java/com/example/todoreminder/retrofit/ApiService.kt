package com.example.todoreminder.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/register")
    suspend fun register(@Body request: LoginRequest): LoginResponse
}