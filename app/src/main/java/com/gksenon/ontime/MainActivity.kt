package com.gksenon.ontime

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gksenon.ontime.ui.TimerInProgressScreen
import com.gksenon.ontime.ui.TimerInitScreen
import com.gksenon.ontime.ui.TimerRingingScreen
import com.gksenon.ontime.ui.theme.OnTimeTheme
import com.gksenon.ontime.viewmodel.TimerInProgressViewModel
import com.gksenon.ontime.viewmodel.TimerInitViewModel
import com.gksenon.ontime.viewmodel.TimerRingingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Provider
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var timerInitViewModelProvider: Provider<TimerInitViewModel>

    @Inject
    lateinit var timerInProgressViewModelProvider: Provider<TimerInProgressViewModel>

    @Inject
    lateinit var timerRingingViewModelProvider: Provider<TimerRingingViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnTimeTheme {
                val backStack = rememberNavBackStack(TimerInit)
                NavDisplay(
                    backStack = backStack,
                    entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
                    entryProvider = entryProvider {
                        entry<TimerInit> {
                            val viewModel = remember { timerInitViewModelProvider.get() }
                            TimerInitScreen(
                                viewModel = viewModel,
                                navigateToTimerInProgress = { duration ->
                                    backStack.addLast(TimerInProgress(duration.inWholeSeconds))
                                    backStack.removeFirst()
                                },
                                navigateToZenModeSettings = {
                                    val intent = Intent("android.settings.ZEN_MODE_SETTINGS")
                                    startActivity(intent)
                                }
                            )
                        }
                        entry<TimerInProgress> { key ->
                            val viewModel = remember {
                                timerInProgressViewModelProvider.get().apply {
                                    start(key.duration.seconds)
                                }
                            }
                            TimerInProgressScreen(
                                viewModel = viewModel,
                                navigateToInitScreen = {
                                    backStack.addFirst(TimerInit)
                                    backStack.removeLast()
                                },
                                navigateToRingingScreen = {
                                    backStack.addLast(TimerRinging)
                                    backStack.replaceAll { key ->
                                        if (key is TimerInProgress) TimerInit else key
                                    }
                                }
                            )
                        }
                        entry<TimerRinging> {
                            val viewModel = remember { timerRingingViewModelProvider.get() }
                            TimerRingingScreen(
                                viewModel = viewModel,
                                navigateToInit = { backStack.removeLast() }
                            )
                        }
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

@Serializable
object TimerInit : NavKey

@Serializable
data class TimerInProgress(val duration: Long) : NavKey

@Serializable
object TimerRinging : NavKey
