package com.gksenon.ontime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.gksenon.ontime.ui.TimerInProgressScreen
import com.gksenon.ontime.ui.TimerInitScreen
import com.gksenon.ontime.ui.TimerRingingScreen
import com.gksenon.ontime.ui.theme.OnTimeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnTimeTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = TimerInit) {
                    composable<TimerInit> {
                        TimerInitScreen(navigateToTimerInProgress = { duration ->
                            val navOptions = navOptions {
                                popUpTo(TimerInit) { inclusive = true }
                            }
                            navController.navigate(
                                route = TimerInProgress(duration.inWholeSeconds),
                                navOptions = navOptions
                            )
                        })
                    }
                    composable<TimerInProgress> {
                        val navOptions = navOptions {
                            popUpTo(TimerInProgress::class) { inclusive = true }
                        }
                        TimerInProgressScreen(
                            navigateToInitScreen = {
                                navController.navigate(route = TimerInit, navOptions = navOptions)
                            },
                            navigateToRingingScreen = {
                                navController.navigate(route = TimerRinging, navOptions = navOptions)
                            }
                        )
                    }
                    composable<TimerRinging> {
                        TimerRingingScreen(navigateToInit = {
                            val navOptions = navOptions {
                                popUpTo(TimerRinging) { inclusive = true }
                            }
                            navController.navigate(route = TimerInit, navOptions = navOptions)
                        })
                    }
                }
            }
        }
    }
}

@Serializable
object TimerInit

@Serializable
data class TimerInProgress(val duration: Long)

@Serializable
object TimerRinging
