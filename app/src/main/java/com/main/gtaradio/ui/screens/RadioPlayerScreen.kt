package com.main.gtaradio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.main.gtaradio.data.GtaGame
import com.main.gtaradio.viewmodel.RadioPlayerViewModel


@Composable
fun RadioPlayerScreen(
    game: GtaGame,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RadioPlayerViewModel = viewModel()
) {
    LaunchedEffect(game.id) {
        viewModel.initializePlayer(game)
    }

    // Фон
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // Контент поверх фона
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Название текущей станции
            viewModel.currentStationName?.let { name ->
                Text(
                    text = name.replace(".m4a", "").replace("_", " "),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Кнопки управления
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 24.dp)
            ) {
                IconButton(
                    onClick = { viewModel.previousStation() },
                    modifier = Modifier.clip(CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, "Предыдущая", tint = Color.White)
                }

                IconButton(
                    onClick = { viewModel.toggleMute() },
                    modifier = Modifier.clip(CircleShape)
                ) {
                    val icon = if (viewModel.isMuted) Icons.Default.PlayArrow else Icons.Default.ThumbUp
                    Icon(icon, "Громкость", tint = Color.White)
                }

                IconButton(
                    onClick = { viewModel.nextStation() },
                    modifier = Modifier.clip(CircleShape)
                ) {
                    Icon(Icons.Default.ArrowForward, "Следующая", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}