package com.example.socialmediaapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.socialmediaapp.R
import com.example.socialmediaapp.ui.messenger.Messenger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        navController = findNavController(R.id.container)
        bottom_menu.setItemSelected(R.id.home)
        bottom_menu.setOnItemSelectedListener { id ->
            when (id) {
                R.id.home -> {
                    navController.navigate(R.id.homeFragment)
                }
                R.id.profile -> {
                    navController.navigate(R.id.profileFragment)
                }
                R.id.add_post -> {
                    //  navController.navigate(R.id.publishFragment)
                    startActivity(Intent(this, PublishActivity::class.java))
                    bottom_menu.setItemSelected(R.id.home)
                    navController.navigate(R.id.homeFragment)
                }
                R.id.messenger -> {
                    //  navController.navigate(R.id.publishFragment)
                    startActivity(Intent(this, Messenger::class.java))
                    bottom_menu.setItemSelected(R.id.home)
                    navController.navigate(R.id.homeFragment)
                }
                R.id.videos -> {
                    navController.navigate(R.id.videosFragment)
                }
            }
            bottom_menu.showBadge(R.id.videos, 8)
            bottom_menu.showBadge(R.id.home, 5)


        }
    }



    override fun onResume() {
        super.onResume()


    }
}