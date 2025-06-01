package com.example.todoreminder.retrofit

data class CreateTodoRequest(
    val title: String,
    val completed: Boolean,
    val date: Long,
    val userId: String
)