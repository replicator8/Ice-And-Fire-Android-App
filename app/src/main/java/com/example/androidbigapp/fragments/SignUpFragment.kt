package com.example.androidbigapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import com.example.androidbigapp.R
import com.example.androidbigapp.SingleActivity
import com.example.androidbigapp.extensions.debugging
import com.example.androidbigapp.model.User

class SignUpFragment: Fragment(R.layout.fragment_sign_up) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        debugging("SignUpFragment - onViewCreated")

        val btnSignUp: Button = view.findViewById(R.id.btnSignUp)
        val btnToSignIn: Button = view.findViewById(R.id.btnUndToSignIn)
        val nameET: EditText = view.findViewById(R.id.etName)
        val ageET: EditText = view.findViewById(R.id.etAge)
        val emailET: EditText = view.findViewById(R.id.etSignUpEmail)
        val password1ET: EditText = view.findViewById(R.id.etSignUpPassword)
        val password2ET: EditText = view.findViewById(R.id.etSignUpPassword2)

        nameET.doOnLayout {
            nameET.requestFocus()
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(nameET, InputMethodManager.SHOW_IMPLICIT)
        }

        btnToSignIn.setOnClickListener {
            (activity as SingleActivity).navigate(SingleActivity.Companion.JUMP_TO_SIGN_IN)
        }

        btnSignUp.setOnClickListener {

            if (emailET.text.isEmpty() || password1ET.text.isEmpty() || nameET.text.isEmpty() || ageET.text.isEmpty() || password2ET.text.isEmpty()) {
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

            if (password1ET.text.length < 5) {
                (activity as SingleActivity).showToast("Password should be more than 5 symbols")
                return@setOnClickListener
            }

            if (password1ET.text.toString() != password2ET.text.toString()) {
                (activity as SingleActivity).showToast("Passwords don't match")
                return@setOnClickListener
            }

            val ageText = ageET.text.toString().trim()
            val ageInt = ageText.toIntOrNull()

            if (ageInt == null || ageInt <= 0) {
                (activity as SingleActivity).showToast("Incorrect age")
                return@setOnClickListener
            }

            if (ageInt > 130) {
                (activity as SingleActivity).showToast("Very big age")
                return@setOnClickListener
            }

            val email = emailET.text.toString()
            val password = password1ET.text.toString()
            val name = nameET.text.toString()

            println("Email: $email")
            println("Password: $password")
            println("Age: $ageInt")
            println("Name: $name")

            val user = User(email, password, name, ageInt)
            val args = Bundle()
            args.putParcelable("EXTRA_USER", user)
            (activity as SingleActivity).navigate(SingleActivity.Companion.JUMP_TO_SIGN_IN, args)
        }
    }
}