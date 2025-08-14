package com.gksenon.ontime.ui

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gksenon.ontime.R
import com.gksenon.ontime.viewmodel.TimerViewModel

@Composable
fun TimerScreen(viewModel: TimerViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    when (state) {
        is TimerViewModel.State.Init -> TimerInitScreen(viewModel::onStartButtonClicked)

        is TimerViewModel.State.InProgress -> TimerInProgressScreen(
            state = state as TimerViewModel.State.InProgress,
            onStopButtonClicked = viewModel::onStopButtonClicked
        )

        is TimerViewModel.State.Ringing -> TimerRingingScreen(viewModel::onMuteButtonClicked)
    }

    val activity = LocalContext.current as Activity
    LaunchedEffect(state.keepScreenOn) {
        if (state.keepScreenOn)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerInitScreen(onStartButtonClicked: (Int, Int, Int) -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = stringResource(R.string.app_name)) })
    }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding() + 32.dp,
                    bottom = innerPadding.calculateBottomPadding() + 32.dp
                )
        ) {
            val hoursPickerState = remember { NumberPickerState() }
            val minutesPickerState = remember { NumberPickerState() }
            val secondsPickerState = remember { NumberPickerState() }
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                val hoursContentDescription = stringResource(R.string.hours_content_description)
                NumberPicker(
                    numbers = (0..23).toList(),
                    state = hoursPickerState,
                    modifier = Modifier.semantics { contentDescription = hoursContentDescription }
                )
                Text(text = ":", fontSize = 32.sp)
                val minutesContentDescription = stringResource(R.string.minutes_content_description)
                NumberPicker(
                    numbers = (0..59).toList(),
                    state = minutesPickerState,
                    modifier = Modifier.semantics { contentDescription = minutesContentDescription }
                )
                Text(text = ":", fontSize = 32.sp)
                val secondsContentDescription = stringResource(R.string.seconds_content_description)
                NumberPicker(
                    numbers = (0..59).toList(),
                    state = secondsPickerState,
                    modifier = Modifier.semantics { contentDescription = secondsContentDescription }
                )
            }
            FilledIconButton(
                onClick = {
                    println("${hoursPickerState.selectedItem},${minutesPickerState.selectedItem}, ${secondsPickerState.selectedItem}")
                    onStartButtonClicked(
                        hoursPickerState.selectedItem,
                        minutesPickerState.selectedItem,
                        secondsPickerState.selectedItem
                    )
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = stringResource(R.string.start)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerInProgressScreen(
    state: TimerViewModel.State.InProgress,
    onStopButtonClicked: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = stringResource(R.string.app_name)) })
    }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding() + 32.dp,
                    bottom = innerPadding.calculateBottomPadding() + 32.dp
                )
        ) {
            val remainingTime = state.remainingTime.toComponents { hours, minutes, seconds, _ ->
                "%02d:%02d:%02d".format(hours, minutes, seconds)
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = remainingTime,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            FilledIconButton(
                onClick = onStopButtonClicked,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_stop_24px),
                    contentDescription = stringResource(R.string.stop)
                )
            }
        }
    }
}

@Composable
fun TimerRingingScreen(onMuteButtonClicked: () -> Unit) {
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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.time_ran_out),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            ElevatedButton(
                onClick = onMuteButtonClicked,
                modifier = Modifier.fillMaxWidth()
            ) { Text(text = stringResource(R.string.mute)) }
        }
    }
}
