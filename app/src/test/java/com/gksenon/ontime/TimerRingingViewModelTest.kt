package com.gksenon.ontime

import com.gksenon.ontime.viewmodel.TimerRingingViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class TimerRingingViewModelTest {

    @Before
    fun init() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun clean() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_showsTimePassed() = runTest {
        val viewModel = TimerRingingViewModel()
        advanceTimeBy(500L)

        val state = viewModel.state.value
        assertEquals(0.seconds, state.timePassed)
        assertFalse(state.navigateToInit)

        viewModel.onTurnOffButtonClicked()
    }

    @Test
    fun onTimePassed_updatesTime() = runTest {
        val viewModel = TimerRingingViewModel()
        advanceTimeBy(1500L)

        val state = viewModel.state.value
        assertEquals(1.seconds, state.timePassed)

        viewModel.onTurnOffButtonClicked()
    }

    @Test
    fun onTurnOffButtonClicked_navigatesToInit() = runTest {
        val viewModel = TimerRingingViewModel()
        viewModel.onTurnOffButtonClicked()
        advanceTimeBy(500L)

        val state = viewModel.state.value
        assertTrue(state.navigateToInit)
    }
}