package com.example.androidbigapp.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.androidbigapp.R
import com.example.androidbigapp.databinding.FragmentHomeBinding
import com.example.androidbigapp.extensions.debugging

class HomeFragment: Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding ?: throw RuntimeException()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.debugging("HomeFragment - onViewCreated")
        _binding = FragmentHomeBinding.bind(view)

        val args: HomeFragmentArgs by navArgs()
        val email = args.EMAIL
        val password = args.PASSWORD
        val isAdmin = args.ADMIN

        if (email != null  && password != null) {
            println("USER EMAIL: $email")
            println("USER EMAIL: $password")
            binding.userEmailOnboard.text = email
        }

        if (isAdmin != null) {
            binding.userEmailOnboard.text = "ADMIN"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}