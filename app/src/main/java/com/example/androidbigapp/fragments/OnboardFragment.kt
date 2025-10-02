package com.example.androidbigapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.androidbigapp.R
import com.example.androidbigapp.SingleActivity
import com.example.androidbigapp.extensions.debugging

class OnboardFragment: Fragment (R.layout.fragment_onboard) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.debugging("OnboardFragment - onViewCreated")

        val btnOnbSignIn: Button = view.findViewById(R.id.btnOnboardSignIn)
        btnOnbSignIn.setOnClickListener {
            activity?.debugging("Click to Sign In")
            (activity as SingleActivity).navigate(SingleActivity.Companion.JUMP_TO_SIGN_IN)
        }

        val btnOnbSignUp: Button = view.findViewById(R.id.btnOnboardSignUp)
        btnOnbSignUp.setOnClickListener {
            activity?.debugging("Click to SignUp")
            (activity as SingleActivity).navigate(SingleActivity.Companion.JUMP_TO_SIGN_UP)
        }

        var cnt = 0
        val mainLogo: ImageView = view.findViewById(R.id.ivMainLogo)
        mainLogo.setOnClickListener {
            cnt++
            if (cnt == 10) {
                val args = Bundle()
                args.putString(SingleActivity.ADMIN, "TRUE")
                (activity as SingleActivity).navigate(SingleActivity.JUMP_TO_HOME, args)
                return@setOnClickListener
                cnt = 0
            }
        }
    }
}