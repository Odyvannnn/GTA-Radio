package com.main.gtaradio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.main.gtaradio.ui.screens.GameSelectionScreen
import com.main.gtaradio.ui.screens.RadioPlayerScreen
import com.main.gtaradio.ui.theme.GTARadioTheme
import com.main.gtaradio.viewmodel.GameSelectionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GTARadioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val gameSelectionViewModel: GameSelectionViewModel = viewModel()

                    // Проверка доступности игр один раз при старте
                    LaunchedEffect(Unit) {
                        gameSelectionViewModel.loadGameAvailability()
                    }

                    NavHost(
                        navController = navController,
                        startDestination = "game_selection"
                    ) {
                        composable("game_selection") {
                            GameSelectionScreen(
                                games = gameSelectionViewModel.games.collectAsState().value,
                                onGameSelected = { game ->
                                    navController.navigate("radio_player/${game.id}")
                                }
                            )
                        }

                        composable("radio_player/{gameId}") { backStackEntry ->
                            val gameId = backStackEntry.arguments?.getString("gameId")
                                ?: run {
                                    // Если gameId отсутствует — назад
                                    navController.popBackStack()
                                    return@composable
                                }

                            val allGames by gameSelectionViewModel.games.collectAsState()
                            val selectedGame = allGames.find { it.id == gameId }

                            if (selectedGame != null) {
                                RadioPlayerScreen(
                                    game = selectedGame,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
