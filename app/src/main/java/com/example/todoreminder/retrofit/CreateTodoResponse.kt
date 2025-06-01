package com.example.todoreminder.retrofit

data class CreateTodoResponse(
    val message: String,
    val id: Int,
    val success: Boolean,
    val error: String? = null
)