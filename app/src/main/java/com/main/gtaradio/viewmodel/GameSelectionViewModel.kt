package com.main.gtaradio.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.main.gtaradio.data.GtaGame
import com.main.gtaradio.data.GtaGamesCatalog
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
            val updatedGames = GtaGamesCatalog.allGames.map { game ->
                game.copy(isAvailable = checkGameAvailability(game, context))
            }
            _games.value = updatedGames
        }
    }

    private fun checkGameAvailability(game: GtaGame, context: Context): Boolean {
        val gameDir = File(context.getExternalFilesDir(null), "radio/${game.id}")
        return game.stations.any { fileName ->
            File(gameDir, fileName).exists()
        }
    }
}