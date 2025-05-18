package com.example.todo.data.api

import com.example.todo.data.model.Todo
import retrofit2.http.GET

interface TodoApi {
    @GET("todos")
    suspend fun getTodos(): List<Todo>
} 