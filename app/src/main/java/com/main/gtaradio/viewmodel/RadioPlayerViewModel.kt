package com.main.gtaradio.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.main.gtaradio.data.GtaGame
import com.main.gtaradio.service.RadioPlaybackService

class RadioPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private var currentGame: GtaGame? = null
    private var currentStationIndex by mutableStateOf(0)
    var isPlaying by mutableStateOf(true)
        private set
    private var playbackService: RadioPlaybackService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RadioPlaybackService.LocalBinder
            playbackService = binder.getService()
            isBound = true

            // 🔑 Подписываемся на изменения состояния
            playbackService?.onPlaybackStateChanged = { isPlaying ->
                this@RadioPlayerViewModel.isPlaying = isPlaying
            }
            // Передаём текущую станцию сервису
            currentGame?.let { game ->
                playbackService?.playStation(game.id, currentStationIndex)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            playbackService = null
        }
    }

    fun initializePlayer(game: GtaGame) {
        currentGame = game
        currentStationIndex = 0
        isPlaying = true

        // Запускаем и привязываем сервис
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, RadioPlaybackService::class.java)
        context.startForegroundService(intent)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun nextStation() {
        val game = currentGame ?: return
        currentStationIndex = (currentStationIndex + 1) % game.stations.size
        playbackService?.playStation(game.id, currentStationIndex)
    }

    fun previousStation() {
        val game = currentGame ?: return
        currentStationIndex = (currentStationIndex - 1 + game.stations.size) % game.stations.size
        playbackService?.playStation(game.id, currentStationIndex)
    }

    fun togglePlayback() {
        isPlaying = !isPlaying
        playbackService?.togglePlayback(isPlaying)
    }

    val currentStationName: String?
        get() = currentGame?.stations?.getOrNull(currentStationIndex)
            ?.file
            ?.replace(".m4a", "")
            ?.replace(".mp3", "")
            ?.replace("_", " ")

    override fun onCleared() {
        val context = getApplication<Application>().applicationContext
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
    }
}