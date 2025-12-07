package com.main.gtaradio.data

import com.main.gtaradio.R

object GtaGamesCatalog {
    val allGames = listOf(
        GtaGame(
            id = "gta_3",
            name = "GTA III",
            iconRes = R.drawable.ic_gta_3,
            stations = listOf("Rise FM.mp3", "MSX FM.mp3", "Lips 106.mp3", "K-JAH.mp3", "Head Radio.mp3", "Game Radio.mp3", "Flashback 95.6.mp3", "Double Clef FM.mp3", "Chatterbox FM.mp3")
        ),
        GtaGame(
            id = "gta_vc",
            name = "GTA: Vice City",
            iconRes = R.drawable.ic_gta_vc,
            stations = listOf("Emotion 98.3.mp3", "Radio Espantoso.mp3", "Fever 105.mp3", "Flash FM.mp3", "K-Chat.mp3", "VCPR.mp3", "V-Rock.mp3", "Wave 103.mp3", "Wildstyle.mp3")
        ),
        GtaGame(
            id = "gta_sa",
            name = "GTA: San Andreas",
            iconRes = R.drawable.ic_gta_sa,
            stations = listOf("Bounce FM.mp3", "CSR 103.9.mp3", "K-DST.mp3", "K-JAH West.mp3", "K-Rose.mp3", "Master Sounds 98.3.mp3", "Playback FM.mp3", "Radio Los Santos.mp3", "Radio X.mp3", "SF-UR.mp3", "West Coast Talk Radio.mp3")
        ),
        GtaGame(
            id = "gta_iv",
            name = "GTA IV",
            iconRes = R.drawable.ic_gta_iv,
            stations = listOf("Electro-Choc.mp3", "Integrity 2.0.mp3", "Jazz Nation Radio 108.5.mp3", "K109 The Studio.mp3", "Liberty City Hardcore.mp3", "Liberty Rock Radio.mp3", "Massive B Soundsystem 96.9.mp3", "Public Liberty Radio.mp3", "Radio Broker.mp3", "RamJam FM.mp3", "San Juan Sounds.mp3", "Self-Actualization FM.mp3", "The Beat 102.7.mp3", "The Classics 104.1.mp3", "The Journey.mp3", "The Vibe 98.8.mp3", "Tuff Gong Radio.mp3", "Vice City FM.mp3", "Vladivostok FM.mp3", "WKTT Radio.mp3")
        ),
        GtaGame(
            id = "gta_v",
            name = "GTA V",
            iconRes = R.drawable.ic_gta_v,
            stations = listOf("Blaine County Radio.mp3", "Blonded Radio.mp3", "Blue Ark.mp3", "Channel X.mp3", "East Los FM.mp3", "FlyLo FM.mp3", "Los Santos Rock Radio.mp3", "Los Santos Underground Radio.mp3", "Non-Stop-Pop FM.mp3", "Radio Los Santos.mp3", "Radio Mirror Park.mp3", "Rebel Radio.mp3", "Soulwax FM.mp3", "Space 103.2.mp3", "The Lab.mp3", "The Lowdown 91.1.mp3", "Vinewood Boulevard Radio.mp3", "West Coast Classics.mp3", "West Coast Talk Radio.mp3", "WorldWide FM.mp3")
        )
    )
}