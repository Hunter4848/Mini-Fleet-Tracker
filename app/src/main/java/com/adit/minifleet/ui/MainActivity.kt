package com.adit.minifleet.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.adit.minifleet.R
import com.adit.minifleet.databinding.ActivityMainBinding
import com.adit.minifleet.model.UserModel
import com.adit.minifleet.viewmodel.FleetViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import android.app.AlertDialog
import com.adit.minifleet.helper.SessionManager

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: FleetViewModel by viewModels()
    private var map: GoogleMap? = null
    private var vehicleMarker: Marker? = null
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        firebaseDatabase.reference.child("Users").child(firebaseUser.uid).get()
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) return@addOnCompleteListener
                val user = task.result.getValue(UserModel::class.java)
                Log.d("TestUser", user.toString())
            }

        viewModel.vehicleData.observe(this) { vehicle ->
            val location = LatLng(vehicle.lat, vehicle.lng)

            if (vehicleMarker == null) {
                vehicleMarker = map?.addMarker(MarkerOptions().position(location).title("Vehicle"))
            } else {
                vehicleMarker?.position = location
            }

            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }

        binding.bottomNavigation.selectedItemId = R.id.navigation_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                R.id.navigation_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
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

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }

        return super.dispatchKeyEvent(event)
    }
}