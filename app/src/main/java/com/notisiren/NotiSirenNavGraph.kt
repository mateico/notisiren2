package com.notisiren

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.notisiren.feature.filters.FilterCreationScreen
import com.notisiren.feature.main.MainScreen

object NotiSirenRoutes {
    const val MAIN = "main"
    const val FILTER_CREATION = "filter_creation"
}

@Composable
fun NotiSirenNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NotiSirenRoutes.MAIN
    ) {
        composable(NotiSirenRoutes.MAIN) {
            MainScreen(
                onAddFilter = {
                    navController.navigate(NotiSirenRoutes.FILTER_CREATION)
                }
            )
        }

        composable(NotiSirenRoutes.FILTER_CREATION) {
            FilterCreationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}