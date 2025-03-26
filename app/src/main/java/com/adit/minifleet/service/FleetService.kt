package com.adit.minifleet.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*

class FleetService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startSimulation()
        return START_STICKY
    }

    private fun startSimulation() {
        serviceScope.launch {
            while (true) {
                Log.d("FleetService", "Simulating vehicle data update...")
                delay(3000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}