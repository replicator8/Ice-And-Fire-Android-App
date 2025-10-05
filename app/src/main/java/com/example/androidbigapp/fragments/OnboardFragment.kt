package com.example.androidbigapp.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidbigapp.R
import com.example.androidbigapp.databinding.FragmentOnboardBinding
import com.example.androidbigapp.extensions.debugging

class OnboardFragment: Fragment (R.layout.fragment_onboard) {

    private var _binding: FragmentOnboardBinding? = null
    private val binding: FragmentOnboardBinding
        get() = _binding ?: throw RuntimeException()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentOnboardBinding.bind(view)
        activity?.debugging("OnboardFragment - onViewCreated")

        binding.btnOnboardSignIn.setOnClickListener {
            activity?.debugging("Click to Sign In")
            findNavController().navigate(R.id.action_screen_start_to_sign_in)
        }

        binding.btnOnboardSignUp.setOnClickListener {
            activity?.debugging("Click to SignUp")
            findNavController().navigate(R.id.action_screen_start_to_sign_up)
        }

        var cnt = 0
        binding.ivMainLogo.setOnClickListener {
            cnt++
            if (cnt == 10) {
                val action = OnboardFragmentDirections.actionScreenStartToHome(ADMIN = "TRUE")
                findNavController().navigate(action)
                cnt = 0
                return@setOnClickListener
            }
        }

        debugging("OnboardFragment.onViewCreated finished")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}