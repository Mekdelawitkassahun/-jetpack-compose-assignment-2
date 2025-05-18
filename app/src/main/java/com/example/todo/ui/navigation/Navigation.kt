package com.example.todo.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todo.ui.screens.TodoDetailScreen
import com.example.todo.ui.screens.TodoListScreen

sealed class Screen(val route: String) {
    data object TodoList : Screen("todoList")
    data object TodoDetail : Screen("todoDetail/{todoId}") {
        fun createRoute(todoId: Int) = "todoDetail/$todoId"
    }
}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.TodoList.route,
        enterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300))
        }
    ) {
        composable(Screen.TodoList.route) {
            TodoListScreen(
                onTodoClick = { todoId ->
                    navController.navigate(Screen.TodoDetail.createRoute(todoId))
                }
            )
        }
        composable(
            route = Screen.TodoDetail.route,
            arguments = listOf(
                navArgument("todoId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getInt("todoId") ?: return@composable
            TodoDetailScreen(
                todoId = todoId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
} 