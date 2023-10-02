package com.example.weatherapp.model

import com.google.gson.JsonObject


data class Main(
    val main: JsonObject,
    val sys: JsonObject,
    val weather: MutableList<Weather>,
    val name: String
)
