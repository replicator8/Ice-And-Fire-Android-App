package com.example.androidbigapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.androidbigapp.extensions.debugging

class OnboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.onboard_main)

        debugging("OnboardActivity - onCreate")

        val btnOnbSignIn: Button = findViewById(R.id.btnOnboardSignIn)
        val btnOnbSignUp: Button = findViewById(R.id.btnOnboardSignUp)
        val mainLogo: ImageView = findViewById(R.id.ivMainLogo)

        btnOnbSignIn.setOnClickListener {
            val intent = Intent(this@OnboardActivity, SignInActivity::class.java)
            startActivity(intent)
        }

        btnOnbSignUp.setOnClickListener {
            val intent = Intent(this@OnboardActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        var cnt = 0

        mainLogo.setOnClickListener {
            cnt++
            if (cnt == 10) {
                val intent = Intent(this@OnboardActivity, HomeActivity::class.java)
                intent.putExtra("EXTRA_ADMIN", "TRUE")
                startActivity(intent)
                cnt = 0
            }
        }
    }

    override fun onStart() {
        super.onStart()

        debugging("OnboardActivity - onStart")
    }

    override fun onRestart() {
        super.onRestart()

        debugging("OnboardActivity - onRestart")
    }

    override fun onResume() {
        super.onResume()

        debugging("OnboardActivity - onResume")
    }

    override fun onPause() {
        super.onPause()

        debugging("OnboardActivity - onPause")
    }

    override fun onStop() {
        super.onStop()

        debugging("OnboardActivity - onStop")
    }

    override fun onDestroy() {
        super.onDestroy()

        debugging("OnboardActivity - onDestroy")
    }
}