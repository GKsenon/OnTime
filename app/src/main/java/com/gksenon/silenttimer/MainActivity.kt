package com.gksenon.silenttimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gksenon.silenttimer.ui.TimerScreen
import com.gksenon.silenttimer.ui.theme.SilentTimerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SilentTimerTheme {
                TimerScreen()
            }
        }
    }
}
