package com.gksenon.silenttimer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gksenon.silenttimer.R
import com.gksenon.silenttimer.viewmodel.TimerViewModel

@Composable
fun TimerScreen(viewModel: TimerViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    when (state) {
        is TimerViewModel.State.Init -> TimerInitScreen(
            state = state as TimerViewModel.State.Init,
            onHoursChanged = viewModel::onHoursChanged,
            onMinutesChanged = viewModel::onMinutesChanged,
            onSecondsChanged = viewModel::onSecondsChanged,
            onStartButtonClicked = viewModel::onStartButtonClicked
        )

        is TimerViewModel.State.InProgress -> TimerInProgressScreen(
            state = state as TimerViewModel.State.InProgress,
            onStopButtonClicked = viewModel::onStopButtonClicked
        )

        is TimerViewModel.State.Ringing -> TimerRingingScreen(viewModel::onMuteButtonClicked)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerInitScreen(
    state: TimerViewModel.State.Init,
    onHoursChanged: (String) -> Unit,
    onMinutesChanged: (String) -> Unit,
    onSecondsChanged: (String) -> Unit,
    onStartButtonClicked: () -> Unit
) {
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                val textFieldColors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
                val hours = if (state.hours > 0) state.hours.toString() else ""
                val hoursContentDescription = stringResource(R.string.hours_content_description)
                TextField(
                    value = hours,
                    onValueChange = onHoursChanged,
                    placeholder = { Text(text = "00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = textFieldColors,
                    modifier = Modifier
                        .width(64.dp)
                        .semantics { contentDescription = hoursContentDescription }
                )
                Text(text = ":")
                val minutes = if (state.minutes > 0) state.minutes.toString() else ""
                val minutesContentDescription = stringResource(R.string.minutes_content_description)
                TextField(
                    value = minutes,
                    onValueChange = onMinutesChanged,
                    placeholder = { Text(text = "00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = textFieldColors,
                    modifier = Modifier
                        .width(64.dp)
                        .semantics { contentDescription = minutesContentDescription }
                )
                Text(text = ":")
                val seconds = if (state.seconds > 0) state.seconds.toString() else ""
                val secondsContentDescription = stringResource(R.string.seconds_content_description)
                TextField(
                    value = seconds,
                    onValueChange = onSecondsChanged,
                    placeholder = { Text(text = "00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = textFieldColors,
                    modifier = Modifier
                        .width(64.dp)
                        .semantics { contentDescription = secondsContentDescription }
                )
            }
            FilledIconButton(
                onClick = onStartButtonClicked,
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(32.dp)
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
