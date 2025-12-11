package com.main.gtaradio.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import androidx.annotation.DrawableRes
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.main.gtaradio.R
import com.main.gtaradio.data.GamesCatalog
import com.main.gtaradio.data.GtaGame
import com.main.gtaradio.data.JsonLoader
import com.main.gtaradio.data.Station
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

class RadioPlaybackService : Service() {

    private lateinit var exoPlayer: ExoPlayer
    private var currentGameId: String? = null
    private var currentStationIndex = 0
    private var isPlaying = false
    private var catalog: GamesCatalog? = null

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val binder = LocalBinder()

    var onPlaybackStateChanged: ((Boolean) -> Unit)? = null


    inner class LocalBinder : Binder() {
        fun getService(): RadioPlaybackService = this@RadioPlaybackService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        try {
            catalog = JsonLoader.loadGamesCatalog(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer.addListener(playerListener)

        startForeground(NOTIFICATION_ID, createNotification("GTA Radio", "Готово к воспроизведению"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                ACTION_PLAY -> {
                    exoPlayer.playWhenReady = true
                    updateNotification()
                }
                ACTION_PAUSE -> {
                    exoPlayer.playWhenReady = false
                    updateNotification()
                }
            }
        }
        return START_NOT_STICKY
    }

    fun playStation(gameId: String, stationIndex: Int) {
        currentGameId = gameId
        currentStationIndex = stationIndex

        val game = catalog?.games?.find { it.id == gameId }
        val station = game?.stations?.getOrNull(stationIndex)
        if (game == null || station == null) return

        val file = File(
            Environment.getExternalStorageDirectory(),
            "GtaRadio/radio/${game.id}/${station.file}"
        )
        if (!file.exists()) return

        exoPlayer.stop()
        exoPlayer.clearMediaItems()

        val mediaItem = MediaItem.fromUri(file.toUri())
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()

        val durationMs = station.duration_ms
        val BASE_TIMESTAMP = 1735689600000L // 1 Jan 2025 UTC
        val now = System.currentTimeMillis()
        val positionMs = (now - BASE_TIMESTAMP) % durationMs

        exoPlayer.seekTo(positionMs)
        exoPlayer.volume = if (isPlaying) 0f else 1f
        exoPlayer.playWhenReady = true

        updateNotification(game, station)
    }

    fun togglePlayback(play: Boolean) {
        if (exoPlayer.playWhenReady) {
            exoPlayer.playWhenReady = false
        } else {
            currentGameId?.let { gameId ->
                playStation(gameId, currentStationIndex)
            }
        }
    }

    private val playerListener: Player.Listener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            val game = catalog?.games?.find { it.id == currentGameId }
            val station = game?.stations?.getOrNull(currentStationIndex)
            if (game != null && station != null) {
                updateNotification(game, station)
            }
        }
        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            onPlaybackStateChanged?.invoke(playWhenReady)
            updateNotification()
        }
    }

    @OptIn(UnstableApi::class)
    private fun updateNotification(game: GtaGame, station: Station) {
        val stationName = station.file
            .replace(".m4a", "")
            .replace("_", " ")

        coroutineScope.launch {
            val iconRes = try {
                game.getIconRes(this as Context)
            } catch (e: Exception) {
                R.drawable.ic_mute
            }

            val iconBitmap = if (iconRes != 0) {
                getBitmapFromDrawable(iconRes)
            } else {
                null
            }

            val notification = createNotification(
                title = game.name,
                text = stationName,
                largeIcon = iconBitmap ?: BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_mute
                )
            )
            try {
                NotificationManagerCompat.from(this@RadioPlaybackService)
                    .notify(NOTIFICATION_ID, notification)
            } catch (e: SecurityException) {
                Log.w("RadioService", "Notification permission denied", e)
            }
        }
    }

    private fun getBitmapFromDrawable(@DrawableRes drawableId: Int): Bitmap? {
        if (drawableId == 0) return null
        return try {
            when (val drawable = ContextCompat.getDrawable(this, drawableId)) {
                is BitmapDrawable -> drawable.bitmap
                else -> drawable?.toBitmap(256, 256)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun createNotification(
        title: String,
        text: String,
        largeIcon: Bitmap? = null
    ): Notification {
        val playIntent = Intent(this, RadioPlaybackService::class.java).apply {
            action = if (exoPlayer.playWhenReady) ACTION_PAUSE else ACTION_PLAY
        }

        val playPendingIntent = PendingIntent.getService(
            this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setLargeIcon(largeIcon)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(Notification.CATEGORY_TRANSPORT)
            // Кнопки управления
            .addAction(
                if (exoPlayer.playWhenReady) R.drawable.ic_pause else R.drawable.ic_play,
                if (exoPlayer.playWhenReady) "Pause" else "Play",
                playPendingIntent
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0)
                    .setMediaSession(null)
            )
            .build()
    }

    private fun updateNotification() {
        val game = catalog?.games?.find { it.id == currentGameId }
        val station = game?.stations?.getOrNull(currentStationIndex)
        if (game != null && station != null) {
            updateNotification(game, station)
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "GTA Radio Playback",
            NotificationManager.IMPORTANCE_LOW
        )
        channel.description = "Музыкальное воспроизведение"
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
        exoPlayer.release()

        stopForeground(STOP_FOREGROUND_REMOVE)

        stopSelf()
    }

    override fun onDestroy() {
        exoPlayer.release()
        coroutineScope.cancel()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancel(NOTIFICATION_ID)

        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "gta_radio_playback_channel"
        const val NOTIFICATION_ID = 1

        const val ACTION_PLAY = "com.main.gtaradio.ACTION_PLAY"
        const val ACTION_PAUSE = "com.main.gtaradio.ACTION_PAUSE"
    }
}