package com.main.gtaradio.data

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

object JsonLoader {
    fun loadGamesCatalog(context: Context): GamesCatalog {
        val jsonReader = InputStreamReader(context.assets.open("games_catalog.json"))
        return Gson().fromJson(jsonReader, GamesCatalog::class.java)
    }
}