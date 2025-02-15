package com.gdg.reactionapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdg.reactionapp.ui.theme.ReactionAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReactionAppTheme {
                ReactionGame()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    ReactionAppTheme {
        ReactionGame()
    }
}


@Composable
fun ReactionGame() {
    var score by remember { mutableStateOf(0) }
    var isGameRunning by remember { mutableStateOf(false) }
    var gameStartTime by remember { mutableStateOf(0L) }
    var beaverStartedTime by remember { mutableStateOf(0L) }
    var beaverCatchedTime = mutableStateListOf<Long>()
    var elapsedTime by remember { mutableStateOf(0f) }
    var targetPosition by remember {
        mutableStateOf(Pair(0f, 0f))
    }

    // Screen dimensions
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val scope = rememberCoroutineScope()
    // Game state management
    LaunchedEffect(isGameRunning) {
        if (isGameRunning) {
            gameStartTime = System.currentTimeMillis()
            while (isGameRunning && elapsedTime < 30f) {
                delay(10)
                elapsedTime = (System.currentTimeMillis() - gameStartTime) / 1000f
            }
            if (elapsedTime >= 30f) {
                isGameRunning = false
            }
        }
    }

    // Generate new random position
    fun moveTarget() {
        beaverStartedTime = System.currentTimeMillis()
        targetPosition = Pair(
            Random.nextFloat() * (screenWidth.value - 48f),
            Random.nextFloat() * (screenHeight.value - 200f)
        )
    }

    // Start new game
    fun startGame() {
        score = 0
        elapsedTime = 0f
        isGameRunning = true
        scope.launch {
            while (isGameRunning) {
                moveTarget()
                delay(1000)
            }

        }


    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .border(1.dp, color = Color.Black)
    ) {
        // Score and Timer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Score: $score",
                fontSize = 24.sp
            )
            Text(
                text = "Time: %.1f".format(elapsedTime),
                fontSize = 24.sp
            )
        }

        // Game target
        if (isGameRunning) {
            Image(
                contentDescription = "",
                painter = painterResource(id = R.drawable.beaver),
                modifier = Modifier
                    .offset(targetPosition.first.dp, targetPosition.second.dp)
                    .size(48.dp)
                    .background(Color(0xFFFF4081), CircleShape)
                    .clickable {
                        if (isGameRunning) {
                            scope.launch {
                                beaverCatchedTime.add(beaverStartedTime - System.currentTimeMillis())
                                score++
                                delay(1000)
                                moveTarget()
                            }

                        }
                    }
            )
        } else {
            // Start/Restart button
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (elapsedTime >= 30f) {
                    Text(
                        text = "Game Over!\nFinal Score: $score \n ${beaverCatchedTime.toList()}",
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                Button(
                    onClick = { startGame() }
                ) {
                    Text(
                        text = if (elapsedTime == 0f) "Start Game" else "Play Again",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}
