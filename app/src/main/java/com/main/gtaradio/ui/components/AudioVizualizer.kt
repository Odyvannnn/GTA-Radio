package com.main.gtaradio.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random

@Composable
fun LcdVisualizer(
    isMuted: Boolean,
    isRadioActive: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 7,
    barColor: Color = Color.Green
) {
    var bars by remember { mutableStateOf(List(barCount) { 0f }) }

    LaunchedEffect(isMuted, isRadioActive) {
        if (!isRadioActive) {
            bars = List(barCount) { 0f }
            return@LaunchedEffect
        }

        // Если мьют включён — фиксируем нейтральное положение
        if (isMuted) {
            // Нейтральное положение: 1 "пиксель" из 8
            val neutralLevel = 1f / 8f // = 0.125f
            bars = List(barCount) { neutralLevel }
            return@LaunchedEffect
        }

        while (isActive) {
            val newBars = List(barCount) { index ->
                val baseHeight = when {
                    index < 2 -> 0.7f
                    index < 5 -> 0.4f
                    else -> 0.25f
                }
                val jitter = (Random.nextFloat() - 0.5f) * 0.2f
                val spike = if (index >= 5 && Random.nextFloat() > 0.85f) 0.4f else 0f
                (baseHeight + jitter + spike).coerceIn(0f, 1f)
            }
            bars = newBars
            delay(150)
        }
    }

    LcdBars(bars = bars, barColor = barColor, modifier = modifier)
}

@Composable
private fun LcdBars(
    bars: List<Float>,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    val barWidth = 4.dp   // Ширина полос
    val barSpacing = 3.dp // Небольшой зазор
    val maxBarHeight = 40.dp

    Row(
        modifier = modifier.height(maxBarHeight),
        horizontalArrangement = Arrangement.spacedBy(barSpacing)
    ) {
        bars.forEach { heightPercent ->
            // Имитация уровней высоты
            val steppedHeight = (heightPercent * 8).toInt().coerceAtMost(8) / 8f

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .fillMaxHeight()
                    .graphicsLayer {
                        scaleY = steppedHeight
                    }
                    .background(barColor)
            )
        }
    }
}