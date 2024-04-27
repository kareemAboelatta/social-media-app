package com.example.socialmediaapp.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMainBinding
import com.example.socialmediaapp.ui.messenger.Messenger
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.container)
        binding.bottomMenu.setItemSelected(R.id.home)
        binding.bottomMenu.setOnItemSelectedListener { id ->
            when (id) {
                R.id.home -> {
                    navController.navigate(R.id.homeFragment)
                }
                R.id.profile -> {
                    navController.navigate(R.id.profileFragment)
                }
                R.id.add_post -> {
                    navController.navigate(R.id.publishFragment)
                }
                R.id.messenger -> {
                    startActivity(Intent(this, Messenger::class.java))
                    binding.bottomMenu.setItemSelected(R.id.home)
                    navController.navigate(R.id.homeFragment)
                }
                R.id.videos -> {
                    navController.navigate(R.id.videosFragment)
                }
            }
            binding.bottomMenu.showBadge(R.id.videos, 8)
            binding.bottomMenu.showBadge(R.id.home, 5)
        }


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
