package com.adit.minifleet.ui

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adit.minifleet.databinding.ActivityLoginBinding
import com.adit.minifleet.helper.SessionManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        requestStoragePermission()
        mAuth = FirebaseAuth.getInstance()

        binding.idContentLogin.btnLogin.setOnClickListener {
            val email = binding.idContentLogin.Email.text.toString()
            val password = binding.idContentLogin.Password.text.toString()

            when {
                email.isEmpty() -> showToast("Username tidak boleh kosong")
                password.isEmpty() -> showToast("Password tidak boleh kosong")
                else -> checkLogin(email, password)
            }
        }

    }

    private fun checkLogin(email: String, password: String) {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Please Wait....")
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
            show()
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            progressDialog.dismiss()
            if (task.isSuccessful && task.result?.user != null) {
                val user = mAuth.currentUser
                user?.let {
                    sessionManager.setLogin(
                        isLoggedIn = true,
                        email = email,
                        password = password
                    )
                }
                login()
            } else {
                showToast("Login Gagal")
            }
        }
    }

    private fun requestStoragePermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        Dexter.withActivity(this)
            .withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showToast("Semua izin diberikan!")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>, token: PermissionToken
                ) {
                    showToast("Izin diperlukan untuk menggunakan fitur ini.")
                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()

    }
    private fun login() {
        startActivity(Intent(this, MainActivity::class.java))
        showToast("Login Sukses")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}