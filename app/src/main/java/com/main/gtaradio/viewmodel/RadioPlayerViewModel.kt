package com.main.gtaradio.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.main.gtaradio.data.GtaGame
import java.io.File

class RadioPlayerViewModel(application: Application) : AndroidViewModel(application) {

    private var exoPlayer: ExoPlayer? = null
    private var currentGame: GtaGame? = null
    private var currentStationIndex by mutableStateOf(0)

    var isMuted by mutableStateOf(false)
        private set

    // Возвращает текущую станцию (имя файла)
    private var _currentStationName by mutableStateOf<String?>(null)
    val currentStationName: String? get() = _currentStationName

    fun initializePlayer(game: GtaGame) {
        currentGame = game
        currentStationIndex = 0
        updateCurrentStationName()
        preparePlayer()
    }

    private fun updateCurrentStationName() {
        val name = currentGame?.stations?.getOrNull(currentStationIndex)
            ?.replace(".mp3", "")
            ?.replace("_", " ")
        _currentStationName = name
    }

    private fun preparePlayer() {
        val context = getApplication<Application>().applicationContext
        val game = currentGame ?: return

        // Освобождаем старый плеер
        exoPlayer?.release()

        // Создаём новый плеер
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            setWakeMode(C.WAKE_MODE_LOCAL)
        }

        playCurrentStation()
    }

    private fun playCurrentStation() {
        val game = currentGame ?: return
        val stationFile = getStationFile(game, currentStationIndex)
        if (!stationFile.exists()) return

        val mediaItem = MediaItem.fromUri(stationFile.toUri())
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()

        val positionMs = calculateRadioPosition(stationFile.length())
        exoPlayer?.seekTo(positionMs)

        exoPlayer?.volume = if (isMuted) 0f else 1f
        exoPlayer?.playWhenReady = true
    }

    private fun getStationFile(game: GtaGame, index: Int): File {
        val context = getApplication<Application>().applicationContext
        val fileName = game.stations[index]
        return File(context.getExternalFilesDir(null), "radio/${game.id}/$fileName")
    }

    // Рассчет позиции в эфире (в миллисекундах)
    private fun calculateRadioPosition(fileSizeBytes: Long): Long {
        // Оценка длительности: 320 кбит/с => ~40_000 байт/сек
        // duration_sec = fileSize / (320_000 / 8) = fileSize / 40_000
        val durationMs = (fileSizeBytes / 40_000L) * 1000L

        val BASE_TIMESTAMP = 1735689600000L // 1 янв 2025 UTC
        val now = System.currentTimeMillis()
        return (now - BASE_TIMESTAMP) % durationMs
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
}