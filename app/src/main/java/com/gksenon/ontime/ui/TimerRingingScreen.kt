package com.gksenon.ontime.ui

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gksenon.ontime.R
import com.gksenon.ontime.viewmodel.TimerRingingViewModel

@Composable
fun TimerRingingScreen(
    viewModel: TimerRingingViewModel = hiltViewModel(),
    navigateToInit: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(
                    top = innerPadding.calculateTopPadding() + 32.dp,
                    bottom = innerPadding.calculateBottomPadding() + 32.dp,
                    start = 32.dp,
                    end = 32.dp
                )
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.time_ran_out),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                val formattedTimePassed =
                    state.timePassed.toComponents { hours, minutes, seconds, _ ->
                        "-%02d:%02d:%02d".format(hours, minutes, seconds)
                    }
                Text(
                    text = formattedTimePassed,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            ElevatedButton(
                onClick = viewModel::onTurnOffButtonClicked,
                modifier = Modifier.fillMaxWidth()
            ) { Text(text = stringResource(R.string.turn_off)) }
        }
    }

    val activity = LocalContext.current as Activity
    DisposableEffect(Unit) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    LaunchedEffect(state.navigateToInit) {
        if(state.navigateToInit) navigateToInit()
    }
}