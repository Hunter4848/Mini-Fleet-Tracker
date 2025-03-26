package com.adit.minifleet.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.adit.minifleet.R
import com.adit.minifleet.databinding.ActivityDashboardBinding
import com.adit.minifleet.helper.SessionManager
import com.adit.minifleet.viewmodel.FleetViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: FleetViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        binding.bottomNavigation.selectedItemId = R.id.navigation_dashboard

        viewModel.vehicleData.observe(this) { vehicle ->
            binding.textSpeed.text = "Speed: ${vehicle.speed} km/h"
            binding.textEngineStatus.text = "Engine: ${if (vehicle.engineOn) "On" else "Off"}"
            binding.textDoorStatus.text = "Door: ${if (vehicle.doorOpen) "Open" else "Closed"}"
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }

                R.id.navigation_dashboard -> {
                    true
                }
                R.id.navigation_setting -> {
                    AlertDialog.Builder(this)
                        .setTitle("Konfirmasi keluar")
                        .setMessage("Apakah Anda yakin ingin keluar?")
                        .setPositiveButton("Ya") { _, _ ->
                            sessionManager.destroySession()
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        .setNegativeButton("Tidak") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                    true
                }
                else -> false
            }
        }

        viewModel.alertEvent.observe(this) { alertMessage ->
            Toast.makeText(this, alertMessage, Toast.LENGTH_SHORT).show()
        }
    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }

        return super.dispatchKeyEvent(event)
    }
}