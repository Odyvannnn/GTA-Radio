package com.main.gtaradio.data

import androidx.annotation.DrawableRes

data class GtaGame(
    val id: String,
    val name: String,
    val iconRes: String,
    val stations: List<Station>,
    var isAvailable: Boolean = false
) {
    @DrawableRes
    fun getIconRes(context: android.content.Context): Int {
        return context.resources.getIdentifier(iconRes, "drawable", context.packageName)
    }
}
