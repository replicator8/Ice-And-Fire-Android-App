package com.example.androidbigapp

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import com.example.androidbigapp.extensions.debugging
import com.example.androidbigapp.model.User

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        debugging("SignUpActivity - onCreate")

        val btnSignUp: Button = findViewById(R.id.btnSignUp)
        val btnToSignIn: Button = findViewById(R.id.btnUndToSignIn)
        val nameET: EditText = findViewById(R.id.etName)
        val ageET: EditText = findViewById(R.id.etAge)
        val emailET: EditText = findViewById(R.id.etSignUpEmail)
        val password1ET: EditText = findViewById(R.id.etSignUpPassword)
        val password2ET: EditText = findViewById(R.id.etSignUpPassword2)

        nameET.doOnLayout {
            nameET.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(nameET, InputMethodManager.SHOW_FORCED)
        }

        btnToSignIn.setOnClickListener {
            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
        }

        btnSignUp.setOnClickListener {

            if (emailET.text.isEmpty() || password1ET.text.isEmpty() || nameET.text.isEmpty() || ageET.text.isEmpty() || password2ET.text.isEmpty()) {
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

            if (password1ET.text.length < 5) {
                Toast.makeText(this, "Password should be more than 5 symbols", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password1ET.text.toString() != password2ET.text.toString()) {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ageText = ageET.text.toString().trim()
            val ageInt = ageText.toIntOrNull()

            if (ageInt == null || ageInt <= 0) {
                Toast.makeText(this, "Incorrect age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (ageInt > 130) {
                Toast.makeText(this, "Very big age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val email = emailET.text.toString()
            val password = password1ET.text.toString()
            val name = nameET.text.toString()

            println("Email: $email")
            println("Password: $password")
            println("Age: $ageInt")
            println("Name: $name")

            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            val user = User(email, password, name, ageInt)
            intent.putExtra("EXTRA_USER", user)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        debugging("SignUpActivity - onStart")
    }

    override fun onRestart() {
        super.onRestart()

        debugging("SignUpActivity - onRestart")
    }

    override fun onResume() {
        super.onResume()

        debugging("SignUpActivity - onResume")
    }

    override fun onPause() {
        super.onPause()

        debugging("SignUpActivity - onPause")
    }

    override fun onStop() {
        super.onStop()

        debugging("SignUpActivity - onStop")
    }

    override fun onDestroy() {
        super.onDestroy()

        debugging("SignUpActivity - onDestroy")
    }
}