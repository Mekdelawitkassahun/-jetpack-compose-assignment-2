package com.example.todo

import android.app.Application
import com.example.todo.data.database.TodoDatabase
import com.example.todo.data.repository.TodoRepository
import com.example.todo.di.AppModule

class TodoApplication : Application() {
    // Database instance
    val database by lazy { TodoDatabase.getDatabase(this) }
    
    // Repository instance
    val repository by lazy { AppModule.provideTodoRepository(this) }

    override fun onCreate() {
        super.onCreate()
    }
} 