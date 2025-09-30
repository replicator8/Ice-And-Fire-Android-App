package com.example.androidbigapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import com.example.androidbigapp.model.User

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        val userFromSignUp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EXTRA_USER", User::class.java)
        } else {
            intent.getParcelableExtra("EXTRA_USER")
        }

        println("HI BRO: $userFromSignUp")

        val btnSignIn: Button = findViewById(R.id.btnSignIn)
        val emailET: EditText = findViewById(R.id.etSignInEmail)
        val passwordET: EditText = findViewById(R.id.etSignInPassword)

        if (userFromSignUp != null) {
            emailET.setText(userFromSignUp.email)
            passwordET.setText(userFromSignUp.password)
        }

        emailET.doOnLayout {
            emailET.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(emailET, InputMethodManager.SHOW_FORCED)
        }

        btnSignIn.setOnClickListener {

            if (emailET.text.isEmpty() || passwordET.text.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (emailET.text.length < 3) {
                Toast.makeText(this, "Please enter correct email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!emailET.text.contains("@")) {
                Toast.makeText(this, "Please enter correct email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordET.text.length < 5) {
                Toast.makeText(this, "Password should be more than 5 symbols", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val email = emailET.text.toString()
            val password = passwordET.text.toString()

            println("Email: $email")
            println("Password: $password")

            val intent = Intent(this@SignInActivity, HomeActivity::class.java)
            intent.putExtra("EXTRA_STRING_EMAIL", email)
            intent.putExtra("EXTRA_STRING_PASSWORD", password)
            startActivity(intent)
        }


    }
}