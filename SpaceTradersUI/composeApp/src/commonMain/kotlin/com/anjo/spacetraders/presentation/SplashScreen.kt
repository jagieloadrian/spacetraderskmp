package com.anjo.spacetraders.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anjo.spacetraders.ui.QuickSilver
import com.anjo.spacetraders.utils.Constants
import com.anjo.spacetraders.utils.Constants.colors
import org.jetbrains.compose.resources.painterResource
import spacetradersui.composeapp.generated.resources.Res
import spacetradersui.composeapp.generated.resources.spaceship

@Composable
fun SplashScreen(runAnimation: Boolean) {
    var animate by remember { mutableStateOf(runAnimation) }
    val animatedProgress by animateFloatAsState(
            targetValue = if (animate) 1f else 0f,
            animationSpec = tween(10000, easing = LinearEasing)
    )

    LaunchedEffect(runAnimation) {
        animate = runAnimation
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center) {
        AnimatedVisibility(runAnimation,
                enter = scaleIn(animationSpec = tween(1500, 8500)),
                exit = scaleOut(tween(1500, 8500))) {
            Image(painterResource(Res.drawable.spaceship), null,
                    contentScale = ContentScale.Inside,
                    colorFilter = ColorFilter.tint(Color.LightGray))
        }
        colors.forEachIndexed { index, color ->
            DiagonalLineAnimation(color, 32.dp.value, 16.dp, index, animatedProgress)
        }
        RainbowLongShadowText(animatedProgress)
    }
}

@Composable
fun DiagonalLineAnimation(color: Color, lineSpacing: Float,
                          strokeWidth: Dp,
                          index: Int, animatedProgress: Float) {
    Canvas(modifier = Modifier.fillMaxSize()
            .background(Color.Transparent)) {

        //  start (left bottom corner) and end line (right top corner)
        val startX = -5f + (lineSpacing * 1.2f) * index
        val startY = size.height + 15f // od góry liczy od 0, nie od dołu
        val endX = size.width
        val endY = 0f + (lineSpacing * 0.7f) * index

        //calculate end point of the line based on animation
        val currentEndX = startX + (endX - startX) * animatedProgress
        val currentEndY = startY + (endY - startY) * animatedProgress
        // drawing line
        drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(currentEndX, currentEndY),
                strokeWidth = strokeWidth.toPx(),
                cap = StrokeCap.Round
        )
    }
}

@Composable
fun RainbowLongShadowText(animatedProgress: Float) {
    val shadowOffset = 1f // Odległość między kolejnymi "warstwami" cienia
    val textMeasurer = rememberTextMeasurer()
    val text = Constants.APP_NAME
    val fontFamily = QuickSilver()
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRainbowLongShadowText(text, shadowOffset, textMeasurer, animatedProgress, fontFamily)
    }
}

fun DrawScope.drawRainbowLongShadowText(
        text: String,
        shadowOffset: Float,
        textMeasurer: TextMeasurer,
        animatedProgress: Float,
        fontFamily: FontFamily
) {
    val charSpacing = (size.width/22) // Odstęp między literami
    val textSize = (size.width / 15).dp
    val startX = (size.width*0.22f)
    val startY = (size.height*0.4f)
    val shadowLength = 80 // Długość cienia
    val animatedColor = Color(red = animatedProgress, green = animatedProgress, blue = animatedProgress)

    text.forEachIndexed { index, char ->
        val shadowColor = colors[index % colors.size]

        // Rysowanie cienia dla każdej litery
        for (i in 0 until shadowLength) {
            translate(
                    left = startX + index * charSpacing + i * shadowOffset * animatedProgress,
                    top = startY + i * shadowOffset * animatedProgress
            ) {
                drawText(
                        text = char.toString(),
                        textSize = textSize,
                        color = shadowColor,
                        textMeasurer = textMeasurer,
                        fontFamily = fontFamily
                )
            }
        }

        // Rysowanie litery głównej (na wierzchu)
        translate(left = startX + index * charSpacing, top = startY) {
            drawText(
                    text = char.toString(),
                    textSize = textSize,
//                    color = Color.White.copy(alpha = animatedProgress),
                    color = animatedColor,
                    textMeasurer = textMeasurer,
                    fontFamily = fontFamily
            )
        }
    }
}

// Helper function to draw text in Compose Multiplatform using TextMeasurer
fun DrawScope.drawText(text: String, textSize: Dp, color: Color, textMeasurer: TextMeasurer,
                       fontFamily: FontFamily) {
    val textLayoutResult = textMeasurer.measure(
            text = text,
            style = TextStyle(
                    color = color,
                    fontSize = textSize.toSp(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontFamily = fontFamily
            )
    )
    drawText(textLayoutResult, topLeft = Offset.Zero)
}