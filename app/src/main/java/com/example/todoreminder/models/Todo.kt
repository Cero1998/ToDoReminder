package com.example.todoreminder.models

import android.location.Location

data class Todo(
    val id: Int? = null,
    var title: String,
    var completed: Boolean,
    var date: Long,
    var userId: String
)