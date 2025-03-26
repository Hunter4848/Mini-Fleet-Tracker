package com.adit.minifleet.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adit.minifleet.model.Vehicle
import com.adit.minifleet.respository.FleetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FleetViewModel @Inject constructor(private val repository: FleetRepository) : ViewModel() {
    private val _vehicleData = MutableLiveData<Vehicle>()
    val vehicleData: LiveData<Vehicle> = _vehicleData

    private val _alertEvent = MutableLiveData<String>()
    val alertEvent: LiveData<String> = _alertEvent

    private var lastSpeedAlert = false
    private var lastDoorAlert = false
    private var lastEngineStatus: Boolean? = null


    init {
        startSimulation()
    }


    private fun startSimulation() {
        viewModelScope.launch {
            while (true) {
                val vehicle = repository.getVehicleData()
                Log.d("FleetService", "Updated Vehicle Data: $vehicle")
                _vehicleData.postValue(vehicle)

                if (vehicle.speed > 80 && !lastSpeedAlert) {
                    _alertEvent.postValue("⚠️ Speed over 80 km/h!")
                    lastSpeedAlert = true
                } else if (vehicle.speed <= 80) {
                    lastSpeedAlert = false
                }

                if (vehicle.doorOpen && vehicle.speed > 0 && !lastDoorAlert) {
                    _alertEvent.postValue("🚨 Door open while moving!")
                    lastDoorAlert = true
                } else if (!vehicle.doorOpen || vehicle.speed == 0) {
                    lastDoorAlert = false
                }

                if (lastEngineStatus == null || lastEngineStatus != vehicle.engineOn) {
                    _alertEvent.postValue(if (vehicle.engineOn) "✅ Engine turned ON" else "❌ Engine turned OFF")
                    lastEngineStatus = vehicle.engineOn
                }

                delay(3000)
            }
        }
    }
}