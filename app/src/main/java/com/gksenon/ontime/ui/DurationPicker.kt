package com.gksenon.ontime.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gksenon.ontime.R
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.time.Duration

@Composable
fun DurationPicker(
    duration: Duration,
    onHoursChanged: (Int) -> Unit,
    onMinutesChanged: (Int) -> Unit,
    onSecondsChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        val (hours, minutes, seconds) = duration.toComponents { hours, minutes, seconds, _ ->
            Triple(hours.toInt(), minutes, seconds)
        }
        val hoursContentDescription = stringResource(R.string.hours_content_description)
        NumberPicker(
            numbers = (0..23).toList(),
            selectedNumber = hours,
            onSelectedNumberChanged = onHoursChanged,
            modifier = Modifier.semantics { contentDescription = hoursContentDescription }
        )
        Text(text = ":", fontSize = 32.sp)
        val minutesContentDescription = stringResource(R.string.minutes_content_description)
        NumberPicker(
            numbers = (0..59).toList(),
            selectedNumber = minutes,
            onSelectedNumberChanged = onMinutesChanged,
            modifier = Modifier.semantics { contentDescription = minutesContentDescription }
        )
        Text(text = ":", fontSize = 32.sp)
        val secondsContentDescription = stringResource(R.string.seconds_content_description)
        NumberPicker(
            numbers = (0..59).toList(),
            selectedNumber = seconds,
            onSelectedNumberChanged = onSecondsChanged,
            modifier = Modifier.semantics { contentDescription = secondsContentDescription }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NumberPicker(
    numbers: List<Int>,
    selectedNumber: Int,
    onSelectedNumberChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val visibleItemsMiddle = 1
    val listScrollCount = Integer.MAX_VALUE
    val listScrollMiddle = listScrollCount / 2
    val listStartIndex = listScrollMiddle - listScrollMiddle % numbers.size - 1

    fun getItem(index: Int) = numbers[index % numbers.size]

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = listStartIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val fadingEdgeGradient = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.5f to Color.Black,
            1f to Color.Transparent
        )
    }

    LaunchedEffect(selectedNumber) {
        val currentItemIndex = listState.firstVisibleItemIndex + visibleItemsMiddle
        val currentItem = getItem(currentItemIndex)
        if (currentItem != selectedNumber) {
            val newItemIndex = currentItemIndex + (selectedNumber - currentItem)
            listState.scrollToItem(newItemIndex - visibleItemsMiddle)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> getItem(index + visibleItemsMiddle) }
            .distinctUntilChanged()
            .collect { item ->
                onSelectedNumberChanged(item)
            }
    }

    val itemSize = 64.dp
    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .height(itemSize * 3)
            .fadingEdge(fadingEdgeGradient)
            .then(modifier)
    ) {
        items(listScrollCount) { index ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(itemSize)
            ) {
                Text(
                    text = "%02d".format(getItem(index)),
                    maxLines = 1,
                    fontSize = 32.sp
                )
            }
        }
    }
}

private fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }
