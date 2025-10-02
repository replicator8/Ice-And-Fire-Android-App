package com.example.androidbigapp.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import com.example.androidbigapp.R
import android.content.Context
import com.example.androidbigapp.SingleActivity
import com.example.androidbigapp.extensions.debugging
import com.example.androidbigapp.model.User

class SignInFragment: Fragment(R.layout.fragment_sign_in) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        debugging("SignInFragment - onViewCreated")

        val userFromSignUp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("EXTRA_USER", User::class.java)
        } else {
            arguments?.getParcelable("EXTRA_USER")
        }

        println("HI BRO: $userFromSignUp")

        val btnSignIn: Button = view.findViewById(R.id.btnSignIn)
        var btnToSignUp: Button = view.findViewById(R.id.btnUndToSignUp)
        val emailET: EditText = view.findViewById(R.id.etSignInEmail)
        val passwordET: EditText = view.findViewById(R.id.etSignInPassword)

        if (userFromSignUp != null) {
            emailET.setText(userFromSignUp.email)
            passwordET.setText(userFromSignUp.password)
        }

        emailET.doOnLayout {
            emailET.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(emailET, InputMethodManager.SHOW_IMPLICIT)
        }

        btnSignIn.setOnClickListener {

            if (emailET.text.isEmpty() || passwordET.text.isEmpty()) {
                (activity as SingleActivity).showToast("Please fill all fields")
                return@setOnClickListener
            }

            if (emailET.text.length < 3) {
                (activity as SingleActivity).showToast("Please enter correct email")
                return@setOnClickListener
            }

            if (!emailET.text.contains("@")) {
                (activity as SingleActivity).showToast("Please enter correct email")
                return@setOnClickListener
            }

            if (passwordET.text.length < 5) {
                (activity as SingleActivity).showToast("Password should be more than 5 symbols")
                return@setOnClickListener
            }

            val email = emailET.text.toString()
            val password = passwordET.text.toString()

            println("Email: $email")
            println("Password: $password")

            val args = Bundle()
            args.putString("EXTRA_STRING_EMAIL", email)
            args.putString("EXTRA_STRING_PASSWORD", password)
            (activity as SingleActivity).navigate(SingleActivity.Companion.JUMP_TO_HOME, args)
        }

        btnToSignUp.setOnClickListener {
            (activity as SingleActivity).navigate(SingleActivity.Companion.JUMP_TO_SIGN_UP)
        }
    }
}