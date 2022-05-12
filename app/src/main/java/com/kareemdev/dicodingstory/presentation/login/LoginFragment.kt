package com.kareemdev.dicodingstory.presentation.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.kareemdev.dicodingstory.MainActivity
import com.kareemdev.dicodingstory.MainActivity.Companion.EXTRA_TOKEN
import com.kareemdev.dicodingstory.R
import com.kareemdev.dicodingstory.databinding.FragmentLoginBinding
import com.kareemdev.dicodingstory.utils.animateVisibility
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, ch

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var loginJob: Job = Job()
    private val viewModel: LoginViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActions()
        /*playAnimation()*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setActions() {
        binding.apply {
            btnRegister.setOnClickListener {  }
            btnLogin.setOnClickListener { goLogin() }
        }
    }

    private fun goLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        setLoading(true)
        lifecycleScope.launchWhenResumed {
            // Make sure only one job that handle the login process
            if (loginJob.isActive) loginJob.cancel()

            loginJob = launch {
                viewModel.login(email, password).collect { result ->
                    result.onSuccess { credentials ->

                        // Save token to the preferences
                        // And direct user to the MainActivity
                        credentials.loginResult?.token?.let { token ->
                            viewModel.saveToken(token)
                            Intent(requireContext(), MainActivity::class.java).also { intent ->
                                intent.putExtra(EXTRA_TOKEN, token)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        }

                        Toast.makeText(
                            requireContext(),
                            getString(R.string.login_success_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    result.onFailure {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.login_error_message),
                            Snackbar.LENGTH_SHORT
                        ).show()

                        setLoading(false)
                    }
                }
            }
        }
    }

    private fun setLoading(b: Boolean) {
        binding.apply {
            etEmail.isEnabled = !b
            etPassword.isEnabled = !b
            btnLogin.isEnabled = !b

            if(b){
                viewLoading.animateVisibility(true)

            }else{
                viewLoading.animateVisibility(false)
            }
        }
    }

    /*private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val message = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(500)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1f)
                .setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 500
        }.start()
    }*/


}