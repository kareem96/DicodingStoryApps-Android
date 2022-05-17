package com.kareemdev.dicodingstory.presentation.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kareemdev.dicodingstory.R
import com.kareemdev.dicodingstory.databinding.ActivityHomeBinding
import com.kareemdev.dicodingstory.presentation.AuthActivity
import com.kareemdev.dicodingstory.presentation.adapter.LoadingAdapter
import com.kareemdev.dicodingstory.presentation.adapter.StoryAdapter
import com.kareemdev.dicodingstory.presentation.add.AddStoryActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalPagingApi
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var storyAdapter: StoryAdapter
    private val viewModel: HomeViewModel by viewModels()

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        showLoading(true)


        storyAdapter = StoryAdapter()
        recyclerView = binding.rvStories
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingAdapter{
                storyAdapter.retry()
            }
        )
        recyclerView.setHasFixedSize(true)
        viewModel.getStories(intent.getStringExtra(EXTRA_TOKEN)!!).observe(this){
            storyAdapter.submitData(lifecycle, it)
            showLoading(false)
        }
        binding.btnCreateStory.setOnClickListener {
            Intent(this, AddStoryActivity::class.java).also {
                startActivity(it)
            }
        }
        binding.btnLogout.setOnClickListener {
            goToLogout()
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun goToLogout() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Are you sure?")
            .setMessage("You need to login again after logout")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Logout") { _, _ ->
                viewModel.logout()
                Intent(this, AuthActivity::class.java).also { intent ->
                    startActivity(intent)
                }
                Toast.makeText(this, "Success logout", Toast.LENGTH_SHORT).show()
            }
            .show()
    }


}