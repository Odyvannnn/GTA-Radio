package com.main.gtaradio.viewmodel

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.main.gtaradio.data.GtaGame
import com.main.gtaradio.data.JsonLoader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class GameSelectionViewModel(application: Application) : AndroidViewModel(application) {

    private val _games = MutableStateFlow<List<GtaGame>>(emptyList())
    val games: StateFlow<List<GtaGame>> = _games
    fun loadGameAvailability() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            try {
                val catalog = JsonLoader.loadGamesCatalog(context)
                val updatedGames = catalog.games.map { game ->
                    game.copy(isAvailable = checkGameAvailability(game, context))
                }
                _games.value = updatedGames
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkGameAvailability(game: GtaGame, context: Context): Boolean {
        val gameDir = File(
            Environment.getExternalStorageDirectory(),
            "GtaRadio/radio/${game.id}"
        )
        return game.stations.any { station ->
            File(gameDir, station.file).exists()
        }
    }
}