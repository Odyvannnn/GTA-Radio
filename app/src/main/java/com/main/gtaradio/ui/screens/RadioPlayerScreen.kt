package com.main.gtaradio.ui.screens

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.main.gtaradio.data.GtaGame
import com.main.gtaradio.viewmodel.RadioPlayerViewModel
import com.main.gtaradio.R
import com.main.gtaradio.ui.theme.SkewedRectangleShapeLeft
import com.main.gtaradio.ui.theme.SkewedRectangleShapeRight
import com.main.gtaradio.utils.SoundEffectPlayer
import com.main.gtaradio.ui.components.LcdVisualizer


@Composable
fun RadioPlayerScreen(
    game: GtaGame,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RadioPlayerViewModel = viewModel()
) {
    val context = LocalContext.current
    var currentVolume by remember { mutableStateOf(getVolumeLevel(context)) }
    val isMuted = viewModel.isMuted


    DisposableEffect(Unit) {
        val volumeReceiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: android.content.Intent?) {
                if (intent?.action == "android.media.VOLUME_CHANGED_ACTION") {
                    currentVolume = getVolumeLevel(context!!)
                }
            }
        }

        context.registerReceiver(
            volumeReceiver,
            android.content.IntentFilter("android.media.VOLUME_CHANGED_ACTION")
        )

        onDispose {
            context.unregisterReceiver(volumeReceiver)
        }
    }



    LaunchedEffect(game.id) {
        viewModel.initializePlayer(game)
    }

    // Фон
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Экран магнитолы
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Black.copy(alpha = 0.8f), shape = MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                LcdVisualizer(
                    isMuted = isMuted,                // из viewModel.isMuted
                    isRadioActive = true,             // радио активно, пока на экране
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .align(Alignment.BottomCenter)
                )
                Text(
                    text = viewModel.currentStationName ?: "—",
                    color = Color.Green,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                if (isMuted) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mute),
                        contentDescription = "Звук отключён",
                        tint = Color.Green,
                        modifier = Modifier
                            .padding(6.dp)
                            .size(20.dp)
                            .align(Alignment.TopEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Нижняя панель: крутилка + кнопки
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Крутилка громкости
                VolumeKnob(
                    volumeLevel = currentVolume,
                    isMuted = isMuted,
                    onToggleMute = {
                        viewModel.toggleMute()
                        SoundEffectPlayer.playMute()}
                )

                // Кнопки переключения станций
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    SkewedButton(
                        onClick = {
                            viewModel.previousStation()
                            SoundEffectPlayer.playClick()},
                        shape = SkewedRectangleShapeLeft,
                        modifier = Modifier
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Назад",
                            tint = Color.Green,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    SkewedButton(
                        onClick = {
                            viewModel.nextStation()
                            SoundEffectPlayer.playClick()},
                        shape = SkewedRectangleShapeRight,
                        modifier = Modifier
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_next),
                            contentDescription = "Вперед",
                            tint = Color.Green,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}


private fun getVolumeLevel(context: Context): Float {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    return if (maxVolume > 0) currentVolume.toFloat() / maxVolume else 0f
}

@Composable
fun VolumeKnob(
    volumeLevel: Float,
    isMuted: Boolean,
    onToggleMute: () -> Unit
) {
    val knobSize = 80.dp
    val strokeWidth = 6.dp

    Box(
        modifier = Modifier
            .size(knobSize)
            .clickable { onToggleMute() }
    ) {
        // 1. Фон
        Image(
            painter = painterResource(id = R.drawable.ic_knob),
            contentDescription = if (isMuted) "Звук выключен" else "Громкость",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // 2. Дуга громкости
        if (!isMuted) {
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2f - strokeWidth.toPx()

                drawArc(
                    color = Color.White,
                    startAngle = 90f,
                    sweepAngle = volumeLevel * 360f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2f, radius * 2f),
                    style = Stroke(
                        width = strokeWidth.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }
        }
    }
}


@Composable
fun SkewedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    shape: Shape = RectangleShape,
    content: @Composable () -> Unit
) {
    val backgroundColor = if (isEnabled) Color.DarkGray else Color.Gray

    Box(modifier = modifier
        .height(24.dp)
        .width(70.dp)) {
        // 1. Тень — немного больше и смещена
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 2.dp, y = 2.dp)
                .clip(shape)
                .background(Color.Black.copy(alpha = 0.4f))
        )

        // 2. Основная кнопка
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(backgroundColor)
                .clickable(enabled = isEnabled, onClick = onClick)
                .padding(vertical = 8.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}