package com.gksenon.ontime.ui

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gksenon.ontime.R
import com.gksenon.ontime.viewmodel.TimerInProgressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerInProgressScreen(
    viewModel: TimerInProgressViewModel = hiltViewModel(),
    navigateToInitScreen: () -> Unit,
    navigateToRingingScreen: () -> Unit
) {
    val state by viewModel.state.collectAsState()

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
            ) {
                Text(
                    text = remainingTime,
                    fontSize = 36.sp,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                items(state.flags) { flag ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        val duration = flag.duration.toComponents { hours, minutes, seconds, _ ->
                            "%02d:%02d:%02d".format(hours, minutes, seconds)
                        }
                        Text(
                            text = flag.id.toString(),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = duration,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ElevatedButton(
                    onClick = viewModel::onFlagButtonClicked,
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_flag_24px),
                        contentDescription = stringResource(R.string.timestamp)
                    )
                }
                FilledIconButton(
                    onClick = viewModel::onStopButtonClicked,
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_stop_24px),
                        contentDescription = stringResource(R.string.stop)
                    )
                }
            }
        }
    }

    val activity = LocalContext.current as Activity
    DisposableEffect(Unit) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    LaunchedEffect(state.navigateToRinging) {
        if (state.navigateToRinging) navigateToRingingScreen()
    }

    LaunchedEffect(state.navigateToInit) {
        if (state.navigateToInit) navigateToInitScreen()
    }
}
