package com.example.todoreminder.retrofit

import com.example.todoreminder.models.Todo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/register")
    suspend fun register(@Body request: LoginRequest): LoginResponse

    @POST("/add_todo")
    suspend fun createTodo(@Body request: CreateTodoRequest): CreateTodoResponse

    @GET("/todos/{userId}")
    suspend fun getTodos(@Path("userId") userId: String): List<Todo>

    @PUT("/todos/{id}/complete")
    suspend fun completeTodo(@Path("id") todoId: Int, @Query("completed") completed: Boolean): TodoCompleteResponse

    @DELETE("/todos/{id}")
    suspend fun deleteTodo(@Path("id") id: Int): GenericResponse
}