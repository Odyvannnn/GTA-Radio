package com.main.gtaradio.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.main.gtaradio.data.GtaGame
import java.io.File

class RadioPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private var exoPlayer: ExoPlayer? = null
    private var currentGame: GtaGame? = null
    private var currentStationIndex by mutableStateOf(0)
    var isPlaying by mutableStateOf(false)
        private set


    var isMuted by mutableStateOf(false)
        private set

    private var _currentStationName by mutableStateOf<String?>(null)
    val currentStationName: String? get() = _currentStationName

    fun initializePlayer(game: GtaGame) {
        currentGame = game
        currentStationIndex = 0
        updateCurrentStationName()
        preparePlayer()
    }

    private fun updateCurrentStationName() {
        val station = currentGame?.stations?.getOrNull(currentStationIndex)

        val name = station?.file
            ?.replace(".m4a", "")
            ?.replace("_", " ")

        _currentStationName = name
    }

    private fun preparePlayer() {
        val context = getApplication<Application>().applicationContext
        exoPlayer?.release()

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        exoPlayer = ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true) // handleAudioFocus = true
            .build()

        playCurrentStation()
    }

    private fun playCurrentStation() {
        val game = currentGame ?: return
        val station = game.stations.getOrNull(currentStationIndex) ?: return

        val context = getApplication<Application>().applicationContext
        val stationFile = File(context.getExternalFilesDir(null), "radio/${game.id}/${station.file}")

        if (!stationFile.exists()) return

        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()

        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    exoPlayer?.removeListener(this)

                    // Вычисление позиции эфира
                    val durationMs = station.duration_ms
                    val BASE_TIMESTAMP = 1735689600000L
                    val now = System.currentTimeMillis()
                    val positionMs = (now - BASE_TIMESTAMP) % durationMs

                    exoPlayer?.seekTo(positionMs)
                    exoPlayer?.volume = if (isMuted) 0f else 1f
                    exoPlayer?.playWhenReady = true // ← ГЛАВНОЕ!
                    isPlaying = true


                }
            }
        })

        // Настраиваем медиа
        val mediaItem = MediaItem.fromUri(stationFile.toUri())
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
    }

    // УПРАВЛЕНИЕ

    fun nextStation() {
        val game = currentGame ?: return
        // 1. Обновление индекса
        currentStationIndex = (currentStationIndex + 1) % game.stations.size
        // 2. Обновление названия
        updateCurrentStationName()
        // 3. Запуск воспроизведения
        playCurrentStation()
    }

    fun previousStation() {
        val game = currentGame ?: return
        // 1. Обновление индекса
        currentStationIndex = (currentStationIndex - 1 + game.stations.size) % game.stations.size
        // 2. Обновление названия
        updateCurrentStationName()
        // 3. Запуск воспроизведения
        playCurrentStation()
    }

    fun toggleMute() {
        isMuted = !isMuted
        exoPlayer?.volume = if (isMuted) 0f else 1f
    }

    override fun onCleared() {
        exoPlayer?.release()
        exoPlayer = null
    }

    fun stopPlayer() {
        exoPlayer?.playWhenReady = false
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()
    }
}