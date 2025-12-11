package com.main.gtaradio

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.main.gtaradio.ui.screens.GameSelectionScreen
import com.main.gtaradio.ui.screens.RadioPlayerScreen
import com.main.gtaradio.ui.theme.GTARadioTheme
import com.main.gtaradio.utils.SoundEffectPlayer
import com.main.gtaradio.viewmodel.GameSelectionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermission()
        requestNotificationPermission()

        SoundEffectPlayer.init(applicationContext)


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

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                100
            )
        }
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION
            )
        }
    }

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Уведомления отключены", Toast.LENGTH_SHORT).show()
            }
        }
    }*/

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 101
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundEffectPlayer.release()
    }
}
