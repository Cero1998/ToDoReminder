package com.example.todoreminder.models

import android.location.Location

data class Todo(
    val id: String,
    var title: String,
    var completed: Boolean,
    var date: String? = null,
    var place: Location? = null
)