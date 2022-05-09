package com.kareemdev.dicodingstory

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.kareemdev.dicodingstory.data.local.datastore.LocalDataStore
import com.kareemdev.dicodingstory.databinding.ActivityMainBinding
import com.kareemdev.dicodingstory.presentation.HomeActivity
import com.kareemdev.dicodingstory.utils.Constants

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()

    }

    private fun setupAction() {
        binding.btnLogout.setOnClickListener {
            mainViewModel.logout()
        }
    }

    private fun setupViewModel() {
        var name: String
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(LocalDataStore.getInstance(dataStore))
        )[MainViewModel::class.java]
        mainViewModel.getUser().observe(this){user ->
            if(user.isLogin){
                Constants.token = "Bearer ${user.token}"
                name = user.name
                binding.tvName.text = "Hello ${user.name}"
                binding.apply {
                    // method get

                    getToken(Constants.token)
                }
            }else{
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }

    private fun getToken(token: String) {
        binding.apply {
            if(token.isEmpty()) return
            /*onLoading(true)*/

        }
    }

    private fun onLoading(data: Boolean) {
        val visibilityState = if(data) View.VISIBLE else View.INVISIBLE
        binding.progressBar.visibility = visibilityState
    }
}