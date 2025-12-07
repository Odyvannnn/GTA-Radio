package com.main.gtaradio.data

data class GtaGame(
    val id: String,
    val name: String,
    val iconRes: Int,
    val stations: List<String>,
    var isAvailable: Boolean = false
)
