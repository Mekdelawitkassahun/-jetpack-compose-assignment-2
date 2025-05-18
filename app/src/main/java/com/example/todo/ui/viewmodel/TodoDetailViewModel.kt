package com.example.todo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.model.Todo
import com.example.todo.data.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoDetailViewModel(
    private val repository: TodoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TodoDetailUiState>(TodoDetailUiState.Loading)
    val uiState: StateFlow<TodoDetailUiState> = _uiState

    fun loadTodo(id: Int) {
        viewModelScope.launch {
            _uiState.value = TodoDetailUiState.Loading
            repository.getTodoById(id).fold(
                onSuccess = { todo ->
                    _uiState.value = TodoDetailUiState.Success(todo)
                },
                onFailure = { error ->
                    _uiState.value = TodoDetailUiState.Error(error.message ?: "Unknown error occurred")
                }
            )
        }
    }
}

sealed class TodoDetailUiState {
    data object Loading : TodoDetailUiState()
    data class Success(val todo: Todo) : TodoDetailUiState()
    data class Error(val message: String) : TodoDetailUiState()
} 