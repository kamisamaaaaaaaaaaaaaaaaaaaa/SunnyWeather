package com.example.sunnyweather.logic.model

data class RealTimeResponse(val status: String, val result: Result) {
    data class Result(val realtime: RealTime)

    data class RealTime(val temperature: String, val skycon: String, val air_quality: AirQuality)

    data class AirQuality(val aqi: AQI)

    data class AQI(val chn: String)
}

