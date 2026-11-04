package com.anjo.spacetraders.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import spacetradersui.composeapp.generated.resources.Res
import spacetradersui.composeapp.generated.resources.TRQuicksilverITCNormal

@Composable
fun QuickSilver() = FontFamily(
        Font(Res.font.TRQuicksilverITCNormal, weight = FontWeight.Medium)
)