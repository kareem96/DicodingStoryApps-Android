package com.kareemdev.dicodingstory.presentation.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kareemdev.dicodingstory.R
import com.kareemdev.dicodingstory.databinding.FragmentRegisterBinding
import com.kareemdev.dicodingstory.utils.animateVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class RegisterFragment : Fragment() {


    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var registerJob: Job = Job()
    private val viewModel: RegisterViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActions()
    }

    private fun setActions() {
        binding.apply {
            btnRegister.setOnClickListener {
                goResgister()
            }
        }
    }

    private fun goResgister() {
        val name = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        stateLoading(true)

        lifecycleScope.launchWhenCreated {
            if(registerJob.isActive) registerJob.cancel()

            registerJob = launch {
                viewModel.registerUser(name, email, password).collect(){results ->
                    results.onSuccess {
                        Toast.makeText(requireContext(), "Registration Success", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }

                    results.onFailure {
                        Snackbar.make(
                            binding.root,
                            "Registration failed, try again!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        stateLoading(false)
                    }
                }
            }
        }
    }

    private fun stateLoading(b: Boolean) {
        binding.apply {
            etEmail.isEnabled = !b
            etFullName.isEnabled = !b
            etPassword.isEnabled = !b
            if(b){
                viewLoading.animateVisibility(true)
            }else{
                viewLoading.animateVisibility(false)
            }
        }
    }
}