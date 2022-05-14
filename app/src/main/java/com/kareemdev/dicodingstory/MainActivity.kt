package com.kareemdev.dicodingstory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.kareemdev.dicodingstory.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.navView, navHostFragment.navController)
        navController.addOnDestinationChangedListener{_, destination, _ ->
            when(destination.id){
                R.id.navigation_home -> showBottomNavigation()
                R.id.navigation_setting -> showBottomNavigation()
                else -> hideBottomNavigation()
            }
        }
    }

    private fun hideBottomNavigation() {
        binding.navView.visibility = View.GONE
    }
    private fun showBottomNavigation() {
        binding.navView.visibility = View.VISIBLE
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }

}