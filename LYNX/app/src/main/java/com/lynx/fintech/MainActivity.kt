package com.lynx.fintech

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lynx.fintech.ui.screens.addtransaction.AddTransactionScreen
import com.lynx.fintech.ui.screens.dashboard.DashboardScreen
import com.lynx.fintech.ui.screens.transactions.TransactionsScreen
import com.lynx.fintech.ui.theme.LynxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LynxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LynxNavGraph()
                }
            }
        }
    }
}

object LynxDestinations {
    const val DASHBOARD = "dashboard"
    const val TRANSACTIONS = "transactions"
    const val ADD_TRANSACTION = "add_transaction"
}

@Composable
fun LynxNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = LynxDestinations.DASHBOARD,
        enterTransition = {
            fadeIn(
                animationSpec = tween(durationMillis = 300, easing = LinearEasing)
            ) + slideInHorizontally(
                animationSpec = tween(durationMillis = 300, easing = LinearEasing),
                initialOffsetX = { it / 4 }
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(durationMillis = 300, easing = LinearEasing)
            ) + slideOutHorizontally(
                animationSpec = tween(durationMillis = 300, easing = LinearEasing),
                targetOffsetX = { -it / 4 }
            )
        },
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(durationMillis = 300, easing = LinearEasing)
            ) + slideInHorizontally(
                animationSpec = tween(durationMillis = 300, easing = LinearEasing),
                initialOffsetX = { -it / 4 }
            )
        },
        popExitTransition = {
            fadeOut(
                animationSpec = tween(durationMillis = 300, easing = LinearEasing)
            ) + slideOutHorizontally(
                animationSpec = tween(durationMillis = 300, easing = LinearEasing),
                targetOffsetX = { it / 4 }
            )
        }
    ) {
        composable(LynxDestinations.DASHBOARD) {
            DashboardScreen(
                onNavigateToTransactions = {
                    navController.navigate(LynxDestinations.TRANSACTIONS)
                },
                onNavigateToAddTransaction = {
                    navController.navigate(LynxDestinations.ADD_TRANSACTION)
                }
            )
        }
        composable(LynxDestinations.TRANSACTIONS) {
            TransactionsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddTransaction = {
                    navController.navigate(LynxDestinations.ADD_TRANSACTION)
                }
            )
        }
        composable(LynxDestinations.ADD_TRANSACTION) {
            AddTransactionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
