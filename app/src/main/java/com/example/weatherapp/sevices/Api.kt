package com.example.weatherapp.sevices

import com.example.weatherapp.model.Main
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//https://api.openweathermap.org/data/2.5/weather?q=London&appid={API key}
//3a1c3e1143a411b627d3ce52a88b9c84
interface Api {

    @GET("weather")
    fun weatherMap(
        @Query("q") cityName: String,
        @Query("appid") api_key: String,

    ):Call<Main>
}