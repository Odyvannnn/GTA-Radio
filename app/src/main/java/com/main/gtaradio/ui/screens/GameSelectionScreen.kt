package com.main.gtaradio.ui.screens

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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val pagerState = rememberPagerState(pageCount = { games.size })

            if (games.isEmpty()) {
                Text("Загрузка...", color = MaterialTheme.colorScheme.onSurface)
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 60.dp), // ← видны края
                    pageSpacing = 20.dp, // ← расстояние между карточками
                    verticalAlignment = Alignment.CenterVertically
                ) { page ->
                    val game = games[page]

                    GameCard(
                        game = game,
                        onClick = { if (game.isAvailable) onGameSelected(game) }
                    )
                }
            }


            Text(
                text = "Выберите игру, чтобы слушать радио",
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun GameCard(
    game: GtaGame,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val iconRes = try {
        game.getIconRes(context)
    } catch (e: Exception) {
        R.drawable.ic_launcher_foreground
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = game.isAvailable,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = if (game.isAvailable) LocalIndication.current else null
            )
    ) {
        // Фон карточки
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        // Изображение
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = game.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        )

        if (!game.isAvailable) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clip(RoundedCornerShape(24.dp))
            )
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Скачайте эту игру",
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
            )
        }
    }
}
