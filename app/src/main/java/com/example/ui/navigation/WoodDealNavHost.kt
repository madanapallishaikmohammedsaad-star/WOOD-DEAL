package com.example.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.WoodDealViewModel
import com.example.ui.screens.DealDetailScreen
import com.example.ui.screens.DealsListScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MoneyScreen
import com.example.ui.screens.NewDealScreen
import com.example.ui.screens.ReportScreen

object AppDestinations {
    const val HOME = "home"
    const val NEW_DEAL = "new_deal"
    const val MY_DEALS = "my_deals"
    const val DEAL_DETAIL = "deal_detail/{dealId}"
    const val MONEY = "money"
    const val REPORT = "report"

    fun createDealDetailRoute(dealId: Long) = "deal_detail/$dealId"
}

@Composable
fun WoodDealNavHost(
    viewModel: WoodDealViewModel,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME,
        modifier = modifier.fillMaxSize()
    ) {
        composable(AppDestinations.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToNewDeal = { navController.navigate(AppDestinations.NEW_DEAL) },
                onNavigateToMyDeals = { navController.navigate(AppDestinations.MY_DEALS) },
                onNavigateToMoney = { navController.navigate(AppDestinations.MONEY) },
                onNavigateToReport = { navController.navigate(AppDestinations.REPORT) }
            )
        }

        composable(AppDestinations.NEW_DEAL) {
            NewDealScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onDealCreated = { newDealId ->
                    viewModel.selectDeal(newDealId)
                    navController.navigate(AppDestinations.createDealDetailRoute(newDealId)) {
                        popUpTo(AppDestinations.HOME)
                    }
                }
            )
        }

        composable(AppDestinations.MY_DEALS) {
            DealsListScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNewDeal = { navController.navigate(AppDestinations.NEW_DEAL) },
                onSelectDeal = { dealId ->
                    viewModel.selectDeal(dealId)
                    navController.navigate(AppDestinations.createDealDetailRoute(dealId))
                }
            )
        }

        composable(
            route = AppDestinations.DEAL_DETAIL,
            arguments = listOf(navArgument("dealId") { type = NavType.LongType })
        ) { backStackEntry ->
            val dealId = backStackEntry.arguments?.getLong("dealId") ?: 0L
            viewModel.selectDeal(dealId)
            DealDetailScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppDestinations.MONEY) {
            MoneyScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onSelectDeal = { dealId ->
                    viewModel.selectDeal(dealId)
                    navController.navigate(AppDestinations.createDealDetailRoute(dealId))
                }
            )
        }

        composable(AppDestinations.REPORT) {
            ReportScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onSelectDeal = { dealId ->
                    viewModel.selectDeal(dealId)
                    navController.navigate(AppDestinations.createDealDetailRoute(dealId))
                }
            )
        }
    }
}
