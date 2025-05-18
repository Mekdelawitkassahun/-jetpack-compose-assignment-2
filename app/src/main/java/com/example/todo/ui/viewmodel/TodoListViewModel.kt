package com.example.todo.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.model.Todo
import com.example.todo.data.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TodoListViewModel(
    private val repository: TodoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TodoListUiState>(TodoListUiState.Loading)
    val uiState: StateFlow<TodoListUiState> = _uiState

    private var currentPage = 0
    private val pageSize = 10
    private var isLoading = false
    private var hasMoreItems = true
    private var allLoadedTodos = mutableListOf<Todo>()

    init {
        Log.d("TodoListViewModel", "Initializing ViewModel")
        loadTodos()
    }

    fun loadTodos() {
        viewModelScope.launch {
            Log.d("TodoListViewModel", "Starting to load todos")
            _uiState.value = TodoListUiState.Loading
            repository.getTodos()
                .catch { e ->
                    Log.e("TodoListViewModel", "Error loading todos", e)
                    _uiState.value = TodoListUiState.Error(e.message ?: "Unknown error occurred")
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { todos ->
                            Log.d("TodoListViewModel", "Successfully loaded ${todos.size} todos")
                            allLoadedTodos.clear()
                            allLoadedTodos.addAll(todos)
                            currentPage = 0
                            loadNextPage()
                        },
                        onFailure = { error ->
                            Log.e("TodoListViewModel", "Failed to load todos", error)
                            _uiState.value = TodoListUiState.Error(error.message ?: "Unknown error occurred")
                        }
                    )
                }
        }
    }

    fun loadNextPage() {
        if (isLoading || !hasMoreItems) return

        isLoading = true
        val startIndex = currentPage * pageSize
        val endIndex = minOf(startIndex + pageSize, allLoadedTodos.size)
        
        if (startIndex >= allLoadedTodos.size) {
            hasMoreItems = false
            isLoading = false
            return
        }

        val pageTodos = allLoadedTodos.subList(startIndex, endIndex)
        val currentTodos = if (currentPage == 0) {
            pageTodos
        } else {
            (_uiState.value as? TodoListUiState.Success)?.todos?.plus(pageTodos) ?: pageTodos
        }

        _uiState.value = TodoListUiState.Success(
            todos = currentTodos,
            hasMoreItems = endIndex < allLoadedTodos.size
        )
        
        currentPage++
        isLoading = false
    }

    fun refreshTodos() {
        Log.d("TodoListViewModel", "Refreshing todos")
        hasMoreItems = true
        currentPage = 0
        loadTodos()
    }
}

sealed class TodoListUiState {
    data object Loading : TodoListUiState()
    data class Success(
        val todos: List<Todo>,
        val hasMoreItems: Boolean
    ) : TodoListUiState()
    data class Error(val message: String) : TodoListUiState()
} 