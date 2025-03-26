package com.adit.minifleet.respository

import com.adit.minifleet.model.Vehicle
import kotlin.random.Random
import javax.inject.Inject

class FleetRepository @Inject constructor() {
    private val gpsCoordinates = listOf(
        Vehicle(-6.1751, 106.8650, 20, true, false),
        Vehicle(-6.1765, 106.8680, 50, true, false),
        Vehicle(-6.1780, 106.8705, 90, true, true)
    )

    private var index = 0

    fun getVehicleData(): Vehicle {
        val vehicle = gpsCoordinates[index]
        index = (index + 1) % gpsCoordinates.size
        return vehicle.copy(speed = Random.nextInt(0, 120))
    }
}