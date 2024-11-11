package com.anjo.spacetraders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.anjo.spacetraders.presentation.SplashScreen
import kotlinx.coroutines.delay

@Composable
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        var showScreen by remember { mutableStateOf(false) }

        LaunchedEffect(showContent) {
            if (showContent) {
//                delay(1000)
                showScreen = showContent
            } else {
                showScreen = showContent
            }
        }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            Column(Modifier.fillMaxWidth()
                    .background(Color.Black), horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedVisibility(showContent,
                        enter = fadeIn(animationSpec = tween(1100, 0)),
                        exit = fadeOut(tween(11000, 0))) {
                    SplashScreen(showScreen)
                }
            }
        }
    }
}