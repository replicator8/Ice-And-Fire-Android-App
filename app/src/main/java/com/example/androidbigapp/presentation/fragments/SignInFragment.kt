package com.example.androidbigapp.presentation.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import com.example.androidbigapp.R
import android.content.Context
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.androidbigapp.presentation.SingleActivity
import com.example.androidbigapp.databinding.FragmentSignInBinding
import com.example.androidbigapp.presentation.extensions.debugging
import kotlin.getValue

class SignInFragment: Fragment(R.layout.fragment_sign_in) {

    private var _binding: FragmentSignInBinding? = null
    private val binding: FragmentSignInBinding
        get() = _binding ?: throw RuntimeException()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        debugging("SignInFragment - onViewCreated")
        _binding = FragmentSignInBinding.bind(view)

        val args: SignInFragmentArgs by navArgs()
        val userFromSignUp = args.USER

        println("HI BRO: $userFromSignUp")

        if (userFromSignUp != null) {
            binding.etSignInEmail.setText(userFromSignUp.email)
            binding.etSignInPassword.setText(userFromSignUp.password)
        }

        binding.etSignInEmail.doOnLayout {
            binding.etSignInEmail.requestFocus()
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etSignInEmail, InputMethodManager.SHOW_IMPLICIT)
        }

        with(binding) {
            btnSignIn.setOnClickListener {

                if (etSignInEmail.text.isEmpty() || etSignInPassword.text.isEmpty()) {
                    (activity as SingleActivity).showToast("Please fill all fields")
                    return@setOnClickListener
                }

                if (etSignInEmail.text.length < 3) {
                    (activity as SingleActivity).showToast("Please enter correct email")
                    return@setOnClickListener
                }

                if (!etSignInEmail.text.contains("@")) {
                    (activity as SingleActivity).showToast("Please enter correct email")
                    return@setOnClickListener
                }

                if (etSignInPassword.text.length < 5) {
                    (activity as SingleActivity).showToast("Password should be more than 5 symbols")
                    return@setOnClickListener
                }

                val email = etSignInEmail.text.toString()
                val password = etSignInPassword.text.toString()

                println("Email: $email")
                println("Password: $password")

                val action = SignInFragmentDirections.actionScreenSignInToHome(EMAIL = email, PASSWORD = password)
                findNavController().navigate(action)
            }
        }

        binding.btnUndToSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_screen_sign_in_to_sign_up)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}