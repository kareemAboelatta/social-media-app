package com.example.socialmediaapp.ui.messenger

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.databinding.ActivityMessengerBinding
import com.example.socialmediaapp.ui.main.ViewModelMain
import com.example.socialmediaapp.firebase.FirebaseService
import com.example.common.ui.utils.Status
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "Messenger"

@AndroidEntryPoint
class Messenger : AppCompatActivity() {

    private val viewModel: ViewModelMain by viewModels()
    private lateinit var binding: ActivityMessengerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessengerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        uploadToken()
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val hostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        val navController = hostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun uploadToken() {
        viewModel.uploadToken()
        viewModel.getUploadState().observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "uploadToken: ${it.data}")
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }

                else -> {}
            }
        }
    }

}
