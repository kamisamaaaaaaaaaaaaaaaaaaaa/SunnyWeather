package com.example.sunnyweather.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.dao.PlaceDao
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.network.SunnyWeatherNetWork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {

    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    fun refreshWeather(lnt: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferedRealTime = async {
                SunnyWeatherNetWork.getRealTimeWeather(lnt, lat)
            }
            val deferedDaily = async {
                SunnyWeatherNetWork.getDailyWeather(lnt, lat)
            }
            val realTimeResponse = deferedRealTime.await()
            val dailyResponse = deferedDaily.await()
            if (realTimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather =
                    Weather(realTimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(RuntimeException("realTime status is ${realTimeResponse.status}, daily status is ${dailyResponse.status}"))
            }
        }
    }

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetWork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            Result.success(placeResponse.places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
}