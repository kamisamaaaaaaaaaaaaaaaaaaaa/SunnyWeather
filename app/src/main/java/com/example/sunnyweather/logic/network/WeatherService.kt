package com.example.sunnyweather.logic.network

import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.DailyResponse
import com.example.sunnyweather.logic.model.RealTimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {
    @GET("v2.5/${SunnyWeatherApplication.Token}/{lnt},{lat}/realtime.json")
    fun getRealTimeWeather(
        @Path("lnt") lnt: String,
        @Path("lat") lat: String
    ): Call<RealTimeResponse>

    @GET("v2.5/${SunnyWeatherApplication.Token}/{lnt},{lat}/daily.json")
    fun getDailyWeather(@Path("lnt") lnt: String, @Path("lat") lat: String): Call<DailyResponse>
}