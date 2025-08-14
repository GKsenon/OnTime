package com.gksenon.ontime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gksenon.ontime.ui.TimerScreen
import com.gksenon.ontime.ui.theme.OnTimeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnTimeTheme {
                TimerScreen()
            }
        }
    }
}
