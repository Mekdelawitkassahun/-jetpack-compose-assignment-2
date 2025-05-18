package com.example.todo.di

import android.content.Context
import android.util.Log
import com.example.todo.data.database.TodoDatabase
import com.example.todo.data.repository.TodoRepository
import com.example.todo.data.api.TodoApi
import com.example.todo.ui.viewmodel.TodoDetailViewModel
import com.example.todo.ui.viewmodel.TodoListViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppModule {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    fun provideTodoDatabase(context: Context): TodoDatabase {
        Log.d("AppModule", "Creating TodoDatabase instance")
        return TodoDatabase.getDatabase(context)
    }

    fun provideTodoApi(): TodoApi {
        Log.d("AppModule", "Creating TodoApi instance with base URL: $BASE_URL")
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        Log.d("AppModule", "Retrofit instance created")
        return retrofit.create(TodoApi::class.java)
    }

    fun provideTodoRepository(context: Context): TodoRepository {
        Log.d("AppModule", "Creating TodoRepository instance")
        val database = provideTodoDatabase(context)
        val api = provideTodoApi()
        return TodoRepository(database.todoDao(), api)
    }

    fun provideTodoListViewModel(context: Context): TodoListViewModel {
        Log.d("AppModule", "Creating TodoListViewModel instance")
        val repository = provideTodoRepository(context)
        return TodoListViewModel(repository)
    }

    fun provideTodoDetailViewModel(context: Context): TodoDetailViewModel {
        Log.d("AppModule", "Creating TodoDetailViewModel instance")
        val repository = provideTodoRepository(context)
        return TodoDetailViewModel(repository)
    }
} 