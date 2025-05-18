package com.example.todo.data.repository

import android.util.Log
import com.example.todo.data.database.TodoDao
import com.example.todo.data.model.Todo
import com.example.todo.data.api.TodoApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull

class TodoRepository(
    private val todoDao: TodoDao,
    private val api: TodoApi
) {
    fun getTodos(): Flow<Result<List<Todo>>> = flow {
        Log.d("TodoRepository", "Starting to get todos")
        
        // First emit cached data if available
        val cachedTodos = todoDao.getAllTodos().firstOrNull()
        if (!cachedTodos.isNullOrEmpty()) {
            Log.d("TodoRepository", "Emitting ${cachedTodos.size} cached todos")
            emit(Result.success(cachedTodos))
        }

        try {
            // Then try to fetch from API
            Log.d("TodoRepository", "Fetching todos from API")
            val apiTodos = api.getTodos()
            Log.d("TodoRepository", "Received ${apiTodos.size} todos from API")
            
            // Update local database
            apiTodos.forEach { todo ->
                todoDao.insertTodo(todo)
            }
            Log.d("TodoRepository", "Updated local database with API todos")
            
            // Emit updated data from local database
            val updatedTodos = todoDao.getAllTodos().firstOrNull()
            if (updatedTodos != null) {
                Log.d("TodoRepository", "Emitting ${updatedTodos.size} updated todos")
                emit(Result.success(updatedTodos))
            }
        } catch (e: Exception) {
            Log.e("TodoRepository", "Error fetching todos from API", e)
            // If there's an error and we have cached data, keep showing it
            // If no cached data, emit the error
            if (cachedTodos.isNullOrEmpty()) {
                Log.e("TodoRepository", "No cached data available, emitting error")
                emit(Result.failure(e))
            } else {
                Log.d("TodoRepository", "Using cached data after API error")
            }
        }
    }

    suspend fun getTodoById(id: Int): Result<Todo> {
        Log.d("TodoRepository", "Getting todo by id: $id")
        return try {
            // First try to get from cache
            val cachedTodo = todoDao.getTodoById(id)
            if (cachedTodo != null) {
                Log.d("TodoRepository", "Found todo in cache")
                Result.success(cachedTodo)
            } else {
                Log.d("TodoRepository", "Todo not found in cache, fetching from API")
                // If not in cache, try to fetch from API
                val apiTodos = api.getTodos()
                val todo = apiTodos.find { it.id == id }
                if (todo != null) {
                    Log.d("TodoRepository", "Found todo in API response")
                    todoDao.insertTodo(todo)
                    Result.success(todo)
                } else {
                    Log.e("TodoRepository", "Todo not found in API response")
                    Result.failure(Exception("Todo not found"))
                }
            }
        } catch (e: Exception) {
            Log.e("TodoRepository", "Error getting todo by id", e)
            Result.failure(e)
        }
    }

    suspend fun insertTodo(todo: Todo) {
        Log.d("TodoRepository", "Inserting todo: ${todo.id}")
        todoDao.insertTodo(todo)
    }

    suspend fun updateTodo(todo: Todo) {
        Log.d("TodoRepository", "Updating todo: ${todo.id}")
        todoDao.updateTodo(todo)
    }

    suspend fun deleteTodo(todo: Todo) {
        Log.d("TodoRepository", "Deleting todo: ${todo.id}")
        todoDao.deleteTodo(todo)
    }
} 