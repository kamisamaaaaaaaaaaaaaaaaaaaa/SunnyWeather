package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

class DailyResponse(val status: String, val result: Result) {

    data class Result(val daily: Daily)

    data class Daily(
        val temperature: List<Temperature>,
        val skycon: List<SkyCon>,
        @SerializedName("life_index") val lifeIndex: LifeIndex
    )

    data class Temperature(val max: String, val min: String)

    data class SkyCon(val value: String, val date: String)

    data class LifeIndex(
        val coldRisk: List<LifeDesc>,
        val carWashing: List<LifeDesc>,
        val ultraviolet: List<LifeDesc>,
        val dressing: List<LifeDesc>
    )

    data class LifeDesc(val desc: String)
}