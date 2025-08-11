package com.gksenon.silenttimer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gksenon.silenttimer.R
import com.gksenon.silenttimer.viewmodel.DurationPickerViewModel

const val TAG_HOURS = "hours"
const val TAG_MINUTES = "minutes"
const val TAG_SECONDS = "seconds"

@Composable
fun DurationPickerScreen(
    viewModel: DurationPickerViewModel = viewModel(),
    onStartTimer: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
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
            val hoursContentDescription = stringResource(R.string.hours_content_description)
            val minutesContentDescription = stringResource(R.string.minutes_content_description)
            val secondsContentDescription = stringResource(R.string.seconds_content_description)
            TextField(
                value = state.hours.toString(),
                onValueChange = viewModel::onHoursChanged,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors,
                modifier = Modifier
                    .width(64.dp)
                    .semantics { contentDescription = hoursContentDescription }
            )
            Text(text = ":")
            TextField(
                value = state.minutes.toString(),
                onValueChange = viewModel::onMinutesChanged,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors,
                modifier = Modifier
                    .width(64.dp)
                    .semantics { contentDescription = minutesContentDescription }
            )
            Text(text = ":")
            TextField(
                value = state.seconds.toString(),
                onValueChange = viewModel::onSecondsChanged,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors,
                modifier = Modifier
                    .width(64.dp)
                    .semantics { contentDescription = secondsContentDescription }
            )
        }
        FilledIconButton(
            onClick = {
                val duration = state.hours * 3600 + state.minutes * 60 + state.seconds
                onStartTimer(duration)
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
