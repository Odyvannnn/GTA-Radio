package com.main.gtaradio.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.main.gtaradio.R


object SoundEffectPlayer {
    private var soundPool: SoundPool? = null
    private var clickSoundId = 0
    private var muteSoundId = 0

    fun init(context: Context) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

        clickSoundId = soundPool!!.load(context, R.raw.button_beep, 1)
        muteSoundId = soundPool!!.load(context, R.raw.mute_sound, 1)
    }

    fun playClick() {
        soundPool?.play(clickSoundId, 0.3f, 0.3f, 0, 0, 1f)
    }

    fun playMute() {
        soundPool?.play(muteSoundId, 0.5f, 0.5f, 0, 0, 1f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}