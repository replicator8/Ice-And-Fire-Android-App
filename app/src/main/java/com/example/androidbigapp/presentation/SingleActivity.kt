package com.example.androidbigapp.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.androidbigapp.R
import com.example.androidbigapp.presentation.extensions.debugging
import com.google.android.material.snackbar.Snackbar

class SingleActivity: AppCompatActivity(R.layout.activity) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        debugging("SingleActivity - onCreate")

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navController = findNavController(R.id.nav_host_fragment)
                if (!navController.popBackStack()) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    fun showSnackBar (message : String ){
        Snackbar.make(findViewById(R.id.nav_host_fragment), message, Snackbar.LENGTH_LONG).show()
    }

    fun showToast (message : String ){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}