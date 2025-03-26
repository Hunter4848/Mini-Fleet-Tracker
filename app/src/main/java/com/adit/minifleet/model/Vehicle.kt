package com.adit.minifleet.model

data class Vehicle(
    val lat: Double,
    val lng: Double,
    val speed: Int,
    val engineOn: Boolean,
    val doorOpen: Boolean
)