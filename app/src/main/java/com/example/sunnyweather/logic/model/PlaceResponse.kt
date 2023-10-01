package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

data class PlaceResponse(val status: String, val places: List<Place>)

data class Place(
    val name: String,
    @SerializedName("formatted_address") val address: String,
    val location: Location
)

data class Location(val lat: String, val lng: String)