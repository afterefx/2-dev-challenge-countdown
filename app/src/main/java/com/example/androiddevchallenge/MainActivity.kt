/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.launch

const val animationMultiplier = 1
const val animationDurationInMillis = 1000 * animationMultiplier

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp(viewModel: MainViewModel = viewModel()) {
    val timeRemaining by viewModel.timeRemaining.observeAsState(0)
    val timerRunning by viewModel.timerRunning.observeAsState(false)

    val scope = rememberCoroutineScope()
    if (timerRunning) {
        val initialValue = viewModel.initialValue.value ?: 0
        scope.launch {
            val startPoint = withFrameMillis { it }
            viewModel.startPoint.value = startPoint
            while (timerRunning && timeRemaining > 0) {
                val elapsedTime = (withFrameMillis { it } - startPoint) / animationDurationInMillis
                val delta = initialValue - elapsedTime
                viewModel.updateSecondsLeft(delta.toInt())
                if (viewModel.debug.value == true) {
                    viewModel.runs.value = viewModel.runs.value?.plus(1) ?: 0
                    viewModel.elapsedTime.value = elapsedTime
                    viewModel.delta.value = delta.toInt()
                }
            }
            viewModel.showFinished()
        }
    }

    MyClock()
}

@Composable
fun FinishScreen(vm: MainViewModel = viewModel()) {
//    val vibrator = LocalContext.current.getSystemService(VIBRATOR_SERVICE) as Vibrator
//    vibrator.vibrate(VibrationEffect.startComposition().compose().  )
    Column(
        Modifier
            .fillMaxSize()
            .clickable {
                vm.resetFinished()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Finished", style = MaterialTheme.typography.h3)
    }
}

@Composable
private fun MyClock(vm: MainViewModel = viewModel()) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TickTock Flop Clock") },
                actions = {
                    IconButton(onClick = { vm.toggleDebug() }) {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Default.BugReport),
                            contentDescription = "debug mode"
                        )
                    }
                }
            )
        }
    ) {

        val finished by vm.finished.observeAsState(false)
        if (finished)
            FinishScreen()
        else {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (
                    debug,
                    buttons,
                    minT,
                    minS,
                    secT,
                    secS,
                    sep
                ) = createRefs()

                val timerRunning by vm.timerRunning.observeAsState(false)
                val minutes = getTimeDisplay() / 60
                val seconds = getTimeDisplay() % 60

                val showDebug by vm.debug.observeAsState(false)
                if (showDebug) {
                    val initialValue by vm.initialValue.observeAsState(0)
                    val timeRemaining by vm.timeRemaining.observeAsState(0)
                    val startPoint by vm.startPoint.observeAsState(0)
                    val elapsedTime by vm.elapsedTime.observeAsState(0)
                    val delta by vm.elapsedTime.observeAsState(0)
                    val runs by vm.runs.observeAsState(0)
                    val incNum by vm.incNum.observeAsState(0)
                    val num by vm.num.observeAsState(0)
                    val decNum by vm.decNum.observeAsState(0)
                    Column(
                        modifier = Modifier.constrainAs(debug) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    ) {
                        Text("Initial value: $initialValue")
                        Text("Timer running: $timerRunning")
                        Text("Time remaining: $timeRemaining")
                        Text("Minutes: $minutes")
                        Text("Seconds: $seconds")
                        Text("Start point: $startPoint")
                        Text("Runs: $runs")
                        Text("Elapsed time: $elapsedTime")
                        Text("Delta: $delta")
                        Text("IncNum: $incNum")
                        Text("Num: $num")
                        Text("DecNum: $decNum")
                    }
                }

                val minutesTenPosition = minutes / 10
                val clickedUp = vm.clickedUp.value ?: true
                Flipper(
                    interval = "Ten Minutes",
                    increment = NumberAction(
                        visible = timerRunning.not(),
                    ) { vm.incrementTenMinutes() },
                    decrement = NumberAction(
                        visible = (timerRunning.not()),
                    ) { vm.decrementTenMinutes() },
                    incNum = incrementedNumber(num = minutesTenPosition),
                    num = minutesTenPosition,
                    decNum = decrementedNumber(num = minutesTenPosition),
                    timerRunning = timerRunning,
                    clickedUp = clickedUp,
                    modifier = Modifier.constrainAs(minT) {
                        top.linkTo(sep.top)
                        bottom.linkTo(sep.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(minS.start)
                    }
                )

                val minutesOnePosition = minutes % 10
                Flipper(
                    interval = "One Minute",
                    increment = NumberAction(
                        visible = timerRunning.not(),
                    ) { vm.incrementMinutes() },
                    decrement = NumberAction(
                        visible = (timerRunning.not()),
                    ) { vm.decrementMinutes() },
                    incNum = incrementedNumber(num = minutesOnePosition),
                    num = minutesOnePosition,
                    decNum = decrementedNumber(num = minutesOnePosition),
                    timerRunning = timerRunning,
                    clickedUp = clickedUp,
                    modifier = Modifier.constrainAs(minS) {
                        top.linkTo(sep.top)
                        bottom.linkTo(sep.bottom)
                        start.linkTo(minT.end)
                        end.linkTo(sep.start)
                    }
                )

                Separator(
                    modifier = Modifier.constrainAs(sep) {
                        top.linkTo(parent.top)
                        bottom.linkTo(buttons.top)
                        start.linkTo(minS.end)
                        end.linkTo(secT.start)
                    }
                )

                val secondsTenPosition = seconds / 10
                Flipper(
                    interval = "Ten Seconds",
                    increment = NumberAction(
                        visible = timerRunning.not(),
                    ) { vm.incrementTenSeconds() },
                    decrement = NumberAction(
                        visible = (timerRunning.not()),
                    ) { vm.decrementTenSeconds() },
                    incNum = incrementedNumber(num = secondsTenPosition),
                    num = secondsTenPosition,
                    decNum = if (seconds > 10) decrementedNumber(num = secondsTenPosition) else 0,
                    timerRunning = timerRunning,
                    clickedUp = clickedUp,
                    modifier = Modifier.constrainAs(secT) {
                        top.linkTo(sep.top)
                        bottom.linkTo(sep.bottom)
                        start.linkTo(sep.end)
                        end.linkTo(secS.start)
                    }
                )

                val secondsOnePosition = seconds % 10
                Flipper(
                    interval = "One Second",
                    increment = NumberAction(
                        visible = timerRunning.not(),
                    ) { vm.incrementSeconds() },
                    decrement = NumberAction(
                        visible = (timerRunning.not()),
                    ) { vm.decrementSeconds() },
                    incNum = incrementedNumber(num = secondsOnePosition),
                    num = secondsOnePosition,
                    decNum = decrementedNumber(num = secondsOnePosition),
                    timerRunning = timerRunning,
                    clickedUp = clickedUp,
                    modifier = Modifier.constrainAs(secS) {
                        top.linkTo(sep.top)
                        bottom.linkTo(sep.bottom)
                        start.linkTo(secT.end)
                        end.linkTo(parent.end)
                    }
                )

                ButtonControls(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(buttons) {
                            bottom.linkTo(parent.bottom, 40.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                )
            }
        }
    }
}

@Composable
private fun ButtonControls(
    modifier: Modifier = Modifier,
    vm: MainViewModel = viewModel(),
) {
    val timerRunning by vm.timerRunning.observeAsState(false)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = { if (timerRunning) vm.stopTimer() else vm.startTimer() },
        ) {
            if (timerRunning) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Default.StopCircle),
                    modifier = Modifier.size(40.dp),
                    contentDescription = "Stop timer"
                )
            } else {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Default.PlayCircleFilled),
                    modifier = Modifier.size(40.dp),
                    contentDescription = "Start timer"
                )
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
        IconButton(onClick = { vm.resetCountdown() }) {
            IconButton(
                onClick = {
                    if (timerRunning) vm.stopTimer()
                    vm.resetCountdown()
                }
            ) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Default.Close),
                    contentDescription = "Stop timer"
                )
            }
        }
    }
}

enum class Half {
    Top, Bottom
}

data class NumberAction(
    val visible: Boolean,
    val onClick: () -> Unit
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Flipper(
    increment: NumberAction,
    decrement: NumberAction,
    num: Int,
    incNum: Int,
    decNum: Int,
    timerRunning: Boolean,
    modifier: Modifier = Modifier,
    interval: String,
    clickedUp: Boolean,
) {
    val vm: MainViewModel = viewModel()
    if (vm.debug.value == true) {
        vm.num.value = num
        vm.incNum.value = incNum
        vm.decNum.value = decNum
    }
    val iconSize = 60.dp
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = increment.visible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            IconButton(onClick = increment.onClick) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    painter = rememberVectorPainter(image = Icons.Default.ArrowDropUp),
                    contentDescription = "Increase $interval"
                )
            }
        }

        var rotation by remember(num) { mutableStateOf(0f) }
        var digitChanged by remember { mutableStateOf(false) }
        LaunchedEffect(key1 = num) {
            digitChanged = true
            val initial = if (timerRunning.not() && clickedUp) 0f else -180f
            val target = if (timerRunning.not() && clickedUp) -180f else 0f
            animate(
                initialValue = initial,
                targetValue = target,
                animationSpec = tween(animationDurationInMillis, easing = LinearEasing)
            ) { value, _ ->
                rotation = value
            }
            if (rotation == target)
                digitChanged = false
        }
        val front = if (clickedUp) decNum else incNum

//        if (digitChanged || timerRunning) {
        Box(Modifier.zIndex(if (clickedUp) 2f else 0f)) {
            // background
            Flap(half = Half.Top, number = if (clickedUp) num else front)

            // starts bottom to top animation
            // ends top to bottom animation
            if (rotation < -89f) {
                Flap(
                    half = Half.Bottom,
                    number = if (clickedUp) num else front,
                    modifier = Modifier
                        .zIndex(if (rotation < -89f) 2f else 0f)
                        .graphicsLayer(rotationX = 180f)
                        .graphicsLayer(
                            rotationX = rotation,
                            transformOrigin = TransformOrigin(.5f, -.025f)
                        )
                )
            }

            Flap(
                half = Half.Top,
                number = if (clickedUp) front else num,
                modifier = Modifier
                    .zIndex(if (rotation < -89f) -2f else 0f)
                    .graphicsLayer(
                        rotationX = rotation,
                        transformOrigin = TransformOrigin(.5f, 1.025f)
                    )
            )
        }
        Spacer(Modifier.height(3.dp))
        Flap(
            half = Half.Bottom,
            number = if (clickedUp) front else num,
            modifier = Modifier.zIndex(if (clickedUp.not()) -2f else 0f)
        )
//        } else if (timerRunning.not() && digitChanged.not()) {
//            Flap(
//                half = Half.Top,
//                number = num,
//            )
//            Spacer(Modifier.height(3.dp))
//            Flap(
//                half = Half.Bottom,
//                number = num,
//            )
//        }

        AnimatedVisibility(
            visible = decrement.visible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
        ) {
            IconButton(onClick = decrement.onClick) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    painter = rememberVectorPainter(image = Icons.Default.ArrowDropDown),
                    contentDescription = "Decrease $interval"
                )
            }
        }
    }
}

@Composable
fun Flap(half: Half, number: Int, modifier: Modifier = Modifier) {
    val addOnModifier = when (half) {
        Half.Bottom ->
            Modifier
                .clip(RoundedCornerShape(0.dp, 0.dp, 4.dp, 4.dp))
                .layout { measurable, _ ->
                    val placeable = measurable.measure(Constraints())
                    val halfHeight = placeable.height / 2
                    layout(placeable.width, halfHeight) {
                        placeable.place(0, -halfHeight)
                    }
                }
        Half.Top ->
            Modifier
                .clip(RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp))
                .layout { measurable, _ ->
                    val placeable = measurable.measure(Constraints())
                    val halfHeight = placeable.height / 2
                    layout(placeable.width, halfHeight) {
                        placeable.place(0, 0)
                    }
                }
    }
    DisplayString(
        modifier = modifier
            .then(addOnModifier)
            .then(
                Modifier
                    .width(80.dp)
                    .background(Color.Gray)
            ),
        string = number.toString(),
    )
}

@Composable
private fun incrementedNumber(num: Int) = if (num == 9) 0 else num + 1

@Composable
private fun decrementedNumber(num: Int) = if (num == 0) 9 else num - 1

@Composable
private fun getTimeDisplay(vm: MainViewModel = viewModel()): Int {
    val timerRunning by vm.timerRunning.observeAsState(false)
    val timeRemaining by vm.timeRemaining.observeAsState(0)
    val initialValue by vm.initialValue.observeAsState(0)
    return if (timerRunning) timeRemaining else initialValue
}

@Composable
fun Separator(modifier: Modifier = Modifier) {
    DisplayString(string = ":", modifier = modifier)
}

@Composable
fun DisplayString(string: String, modifier: Modifier = Modifier) {
    Text(
        text = string,
        fontSize = 80.sp,
        modifier = modifier,
        textAlign = TextAlign.Center
    )
}

@Preview("Dark Theme")
@Composable
fun DarkPreview() {
    MyTheme {
        MyApp()
    }
}
