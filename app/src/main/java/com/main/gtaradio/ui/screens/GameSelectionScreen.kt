package com.main.gtaradio.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.main.gtaradio.data.GtaGame
import com.main.gtaradio.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSelectionScreen(
    games: List<GtaGame>,
    onGameSelected: (GtaGame) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val pagerState = rememberPagerState(pageCount = { games.size })

        if (games.isEmpty()) {
            Text("Загрузка списка игр...", color = MaterialTheme.colorScheme.onSurface)
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
            ) { page ->
                val game = games[page]
                GameCard(
                    game = game,
                    onClick = { if (game.isAvailable) onGameSelected(game) }
                )
            }
        }
    }
}

@SuppressLint("RememberInComposition")
@Composable
fun GameCard(game: GtaGame, onClick: () -> Unit) {
    val context = LocalContext.current // ← Получаем контекст

    Column(

    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clickable(
                    enabled = game.isAvailable,
                    onClick = onClick,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = if (game.isAvailable) LocalIndication.current else null
                )
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            val iconRes = try {
                game.getIconRes(context)
            } catch (e: Exception) {
                R.drawable.ic_launcher_foreground // fallback
            }

            Image(
                painter = painterResource(id = iconRes),
                contentDescription = game.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
            )

            if (!game.isAvailable) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clip(RoundedCornerShape(24.dp))

                )
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Скачайте эту игру",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp)
                )
            }
        }

        Text(
            text = game.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (game.isAvailable) FontWeight.Bold else FontWeight.Normal,
            color = if (game.isAvailable) MaterialTheme.colorScheme.onSurface else Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp)
        )
    }
}