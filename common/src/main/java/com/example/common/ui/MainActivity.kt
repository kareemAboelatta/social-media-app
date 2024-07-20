package com.example.common.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.common.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        checkAndRequestPermission()

    }


    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (!permissions.all { it.value }) checkAndRequestPermission()
        }

    private fun checkAndRequestPermission() {
        val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            arrayOf()
        }

        val isAllPermissionsGranted = permissionsToRequest.all { permissions ->
            ContextCompat.checkSelfPermission(
                applicationContext,
                permissions
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (!isAllPermissionsGranted) requestMultiplePermissions.launch(permissionsToRequest)
    }




}
