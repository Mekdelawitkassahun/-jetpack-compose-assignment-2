package com.example.todo.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todo.data.repository.TodoRepository
import com.example.todo.di.AppModule

class TodoViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = AppModule.provideTodoRepository(context)
        return when {
            modelClass.isAssignableFrom(TodoListViewModel::class.java) -> {
                TodoListViewModel(repository) as? T ?: throw IllegalArgumentException("Failed to cast TodoListViewModel")
            }
            modelClass.isAssignableFrom(TodoDetailViewModel::class.java) -> {
                TodoDetailViewModel(repository) as? T ?: throw IllegalArgumentException("Failed to cast TodoDetailViewModel")
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 