package com.example.todo.data

data class TodoJson(
    val id: Int,
    val todo: String,
    val completed: Boolean,
    val userId: Int
)

data class TodosJson(
    val todos: List<TodoJson>
)