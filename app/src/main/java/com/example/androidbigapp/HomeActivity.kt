package com.example.androidbigapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.androidbigapp.extensions.debugging

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        debugging("HomeActivity - onCreate")

        val emailTV: TextView = findViewById(R.id.userEmailOnboard)

        val email = intent.getStringExtra("EXTRA_STRING_EMAIL")
        val password = intent.getStringExtra("EXTRA_STRING_PASSWORD")
        val isAdmin = intent.getStringExtra("EXTRA_ADMIN")

        println(email)
        println(password)
        if (email != null  && password != null) {
            println("USER EMAIL: $email")
            println("USER EMAIL: $password")
            emailTV.text = email
        }

        if (isAdmin != null) {
            emailTV.text = "ADMIN"
        }
    }

    override fun onStart() {
        super.onStart()

        debugging("HomeActivity - onStart")
    }

    override fun onRestart() {
        super.onRestart()

        debugging("HomeActivity - onRestart")
    }

    override fun onResume() {
        super.onResume()

        debugging("HomeActivity - onResume")
    }

    override fun onPause() {
        super.onPause()

        debugging("HomeActivity - onPause")
    }

    override fun onStop() {
        super.onStop()

        debugging("HomeActivity - onStop")
    }

    override fun onDestroy() {
        super.onDestroy()

        debugging("HomeActivity - onDestroy")
    }
}