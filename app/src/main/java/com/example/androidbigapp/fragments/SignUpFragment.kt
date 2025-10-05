package com.example.androidbigapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidbigapp.R
import com.example.androidbigapp.SingleActivity
import com.example.androidbigapp.databinding.FragmentSignUpBinding
import com.example.androidbigapp.extensions.debugging
import com.example.androidbigapp.model.User

class SignUpFragment: Fragment(R.layout.fragment_sign_up) {

    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding
        get() = _binding ?: throw RuntimeException()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        debugging("SignUpFragment - onViewCreated")
        _binding = FragmentSignUpBinding.bind(view)

        with(binding) {
            etName.doOnLayout {
                etName.requestFocus()
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etName, InputMethodManager.SHOW_IMPLICIT)
            }

            btnUndToSignIn.setOnClickListener {
                findNavController().navigate(R.id.action_screen_sign_up_to_sign_in)
            }

            btnSignUp.setOnClickListener {

                if (etSignUpEmail.text.isEmpty() || etSignUpPassword.text.isEmpty() || etName.text.isEmpty() || etAge.text.isEmpty() || etSignUpPassword2.text.isEmpty()) {
                    (activity as SingleActivity).showToast("Please fill all fields")
                    return@setOnClickListener
                }

                if (etSignUpEmail.text.length < 3) {
                    (activity as SingleActivity).showToast("Please enter correct email")
                    return@setOnClickListener
                }

                if (!etSignUpEmail.text.contains("@")) {
                    (activity as SingleActivity).showToast("Please enter correct email")
                    return@setOnClickListener
                }

                if (etSignUpPassword.text.length < 5) {
                    (activity as SingleActivity).showToast("Password should be more than 5 symbols")
                    return@setOnClickListener
                }

                if (etSignUpPassword.text.toString() != etSignUpPassword2.text.toString()) {
                    (activity as SingleActivity).showToast("Passwords don't match")
                    return@setOnClickListener
                }

                val ageText = etAge.text.toString().trim()
                val ageInt = ageText.toIntOrNull()

                if (ageInt == null || ageInt <= 0) {
                    (activity as SingleActivity).showToast("Incorrect age")
                    return@setOnClickListener
                }

                if (ageInt > 130) {
                    (activity as SingleActivity).showToast("Very big age")
                    return@setOnClickListener
                }

                val email = etSignUpEmail.text.toString()
                val password = etSignUpPassword.text.toString()
                val name = etName.text.toString()

                println("Email: $email")
                println("Password: $password")
                println("Age: $ageInt")
                println("Name: $name")

                val user = User(email, password, name, ageInt)
                val action = SignUpFragmentDirections.actionScreenSignUpToSignIn(USER = user)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}