package com.example.androidbigapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.androidbigapp.R
import com.example.androidbigapp.SingleActivity
import com.example.androidbigapp.extensions.debugging

class HomeFragment: Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.debugging("HomeFragment - onViewCreated")

        val emailTV: TextView = view.findViewById(R.id.userEmailOnboard)

        val email = arguments?.getString("EXTRA_STRING_EMAIL")
        val password = arguments?.getString("EXTRA_STRING_PASSWORD")
        val isAdmin = arguments?.getString(SingleActivity.ADMIN)

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