package com.example.androidbigapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

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
}