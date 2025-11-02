package com.gksenon.ontime.ui

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gksenon.ontime.R
import com.gksenon.ontime.viewmodel.TimerInitViewModel
import kotlinx.coroutines.launch
import kotlin.time.Duration

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TimerInitScreen(
    viewModel: TimerInitViewModel,
    navigateToTimerInProgress: (Duration) -> Unit,
    navigateToZenModeSettings: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = viewModel::onCreatePresetButtonClicked) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = stringResource(R.string.create_preset)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.CenterVertically
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding() + 32.dp,
                    bottom = innerPadding.calculateBottomPadding() + 32.dp
                )
        ) {
            val isZenModeOn by rememberZenModeState()
            if (!isZenModeOn) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = RoundedCornerShape(size = 16.dp)
                        )
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.zen_mode_is_off),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = navigateToZenModeSettings) {
                        Text(text = stringResource(R.string.turn_on_zen_mode))
                    }
                }
            }
            DurationPicker(
                duration = state.duration,
                onHoursChanged = viewModel::onHoursChanged,
                onMinutesChanged = viewModel::onMinutesChanged,
                onSecondsChanged = viewModel::onSecondsChanged,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.presets),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )
            if (state.presets.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                ) {
                    Text(
                        text = stringResource(R.string.no_presets),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 32.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                ) {
                    items(state.presets) { preset ->
                        val backgroundColor =
                            if (state.selectedPresetId == preset.id) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.surfaceDim
                        val borderColor =
                            if (state.selectedPresetId == preset.id) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        Card(
                            colors = CardDefaults.cardColors(containerColor = backgroundColor),
                            shape = CircleShape,
                            border = BorderStroke(width = 1.dp, color = borderColor),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(96.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .combinedClickable(
                                        onClick = { viewModel.onPresetClicked(preset) },
                                        onLongClick = { viewModel.onPresetLongClicked(preset) }
                                    )
                            ) {
                                val formattedDuration =
                                    preset.duration.toComponents { hours, minutes, seconds, _ ->
                                        "%02d:%02d:%02d".format(hours, minutes, seconds)
                                    }
                                Text(text = formattedDuration)
                            }
                        }
                    }
                }
            }
            Button(
                onClick = viewModel::onStartButtonClicked,
                modifier = Modifier.size(width = 256.dp, height = 64.dp)
            ) {
                Text(
                    text = stringResource(R.string.start),
                    fontSize = 20.sp
                )
            }
        }
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        if (state.showEditPresetBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = viewModel::onPresetBottomSheetDismissed,
                sheetState = sheetState
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch { sheetState.hide() }
                                .invokeOnCompletion { viewModel.onEditPresetButtonClicked() }
                        }
                        .padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = stringResource(R.string.edit_preset)
                    )
                    Text(
                        text = stringResource(R.string.edit_preset),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch { sheetState.hide() }
                                .invokeOnCompletion { viewModel.onDeletePresetButtonClicked() }
                        }
                        .padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = stringResource(R.string.delete_preset)
                    )
                    Text(
                        text = stringResource(R.string.delete_preset),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }

    if (state.showCreatePresetDialog) {
        BasicAlertDialog(onDismissRequest = viewModel::onCreatePresetDialogDismissed) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    val title =
                        if (state.presetInEdit != null) R.string.edit_preset_dialog_title
                        else R.string.create_preset
                    Text(
                        text = stringResource(title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth()
                    )
                    val presetDurationPickerContentDescription =
                        stringResource(R.string.preset_duration_picker_content_description)
                    DurationPicker(
                        duration = state.presetDuration,
                        onHoursChanged = viewModel::onPresetHoursChanged,
                        onMinutesChanged = viewModel::onPresetMinutesChanged,
                        onSecondsChanged = viewModel::onPresetSecondsChanged,
                        modifier = Modifier.semantics {
                            contentDescription = presetDurationPickerContentDescription
                        }
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = viewModel::onCreatePresetDialogDismissed) {
                            Text(text = stringResource(android.R.string.cancel))
                        }
                        TextButton(onClick = viewModel::onCreatePresetDialogConfirmed) {
                            Text(text = stringResource(android.R.string.ok))
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(state.navigateToTimerInProgress) {
        if (state.navigateToTimerInProgress) navigateToTimerInProgress(state.duration)
    }
}

@Composable
fun rememberZenModeState(): State<Boolean> {
    val activity = LocalActivity.current
    val notificationManager = activity?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    fun NotificationManager.isZenModeOn() =
        currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL

    return produceState(notificationManager.isZenModeOn()) {
        val interruptionFilterIntentFilter =
            IntentFilter(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)
        val interruptionFilterBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED) {
                    value = notificationManager.isZenModeOn()
                }
            }
        }

        activity.registerReceiver(
            interruptionFilterBroadcastReceiver,
            interruptionFilterIntentFilter
        )

        awaitDispose { activity.unregisterReceiver(interruptionFilterBroadcastReceiver) }
    }
}
