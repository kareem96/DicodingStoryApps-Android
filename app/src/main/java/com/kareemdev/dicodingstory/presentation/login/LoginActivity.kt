package com.kareemdev.dicodingstory.presentation.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.kareemdev.dicodingstory.MainActivity
import com.kareemdev.dicodingstory.R
import com.kareemdev.dicodingstory.ViewModelFactory
import com.kareemdev.dicodingstory.data.local.datastore.LocalDataStore
import com.kareemdev.dicodingstory.data.local.entity.User
import com.kareemdev.dicodingstory.data.model.LoginResponse
import com.kareemdev.dicodingstory.data.remote.ApiConfig
import com.kareemdev.dicodingstory.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {

        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val editEmail = ObjectAnimator.ofFloat(binding.myEtEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val editPassword = ObjectAnimator.ofFloat(binding.myEtPassword, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, email, editEmail, password, editPassword, login)
            startDelay = 600
            start()
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.myEtEmail.text.toString()
            val password = binding.myEtPassword.text.toString()
            when{
                email.isEmpty() -> {
                    binding.myEtEmail.error = ""
                }
                password.isEmpty() -> {
                    binding.myEtPassword.error = ""
                }
                else -> {
                    login()
                }
            }
        }
    }

    private fun login() {
        onLoading(true)
        val client = ApiConfig.getApiService().login(
            binding.myEtEmail.text.toString(),
            binding.myEtPassword.text.toString(),
        )
        client.enqueue(object: Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val responseBody =response.body()
                if(response.isSuccessful && responseBody != null){
                    if(responseBody.error){
                        Toast.makeText(this@LoginActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                    }else{
                        onLoading(false)
                        loginViewModel.saveUser(User(responseBody.loginResult.name, responseBody.loginResult.token, true))
                        AlertDialog.Builder(this@LoginActivity).apply {
                            setTitle("")
                            setMessage("Login Success")
                            setPositiveButton("Ok"){_,_ ->
                                val intent = Intent(context, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                }else{
                    onLoading(false)
                    Toast.makeText(this@LoginActivity, "Login Failed, Wrong Email/Invalid Password", Toast.LENGTH_SHORT).show()
                    Log.e(ContentValues.TAG, "onResponse: ${response.message()}", )
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT).show()
                Log.e(ControlsProviderService.TAG, "onFailure: ${t.message}", )
            }

        })
    }

    private fun onLoading(data: Boolean) {
        binding.progressBar.visibility = if(data) View.VISIBLE else View.INVISIBLE
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(LocalDataStore.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
            )
        }
        supportActionBar?.hide()
    }
}