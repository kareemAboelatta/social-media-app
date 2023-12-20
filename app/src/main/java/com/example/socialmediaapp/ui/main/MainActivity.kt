package com.example.socialmediaapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
                    startActivity(Intent(this, PublishActivity::class.java))
                    binding.bottomMenu.setItemSelected(R.id.home)
                    navController.navigate(R.id.homeFragment)
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
    }


}
