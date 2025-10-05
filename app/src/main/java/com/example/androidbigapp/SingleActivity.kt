package com.example.androidbigapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.androidbigapp.extensions.debugging
import com.example.androidbigapp.fragments.OnboardFragment
import com.example.androidbigapp.fragments.SignInFragment
import com.example.androidbigapp.fragments.SignUpFragment
import com.google.android.material.snackbar.Snackbar
import com.example.androidbigapp.fragments.HomeFragment

class SingleActivity: AppCompatActivity(R.layout.activity) {
    companion object {
        const val JUMP_TO_SIGN_IN = 1
        const val JUMP_TO_SIGN_UP = 2

        const val JUMP_TO_HOME = 3

        const val ADMIN: String = "ADMIN"
        const val BACK_STACK_ONBOARD = "onboard"
        const val BACK_STACK_AUTH = "auth"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        debugging("SingleActivity - onCreate")

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.main, OnboardFragment::class.java, null)
                addToBackStack(BACK_STACK_ONBOARD)
            }
        }
    }

    fun navigate(jump: Int, args: Bundle? = null) {
        when (jump) {
            JUMP_TO_SIGN_IN -> {

                supportFragmentManager.popBackStack(
                    BACK_STACK_AUTH,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )

                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.main, SignInFragment::class.java, args)
                    addToBackStack(BACK_STACK_AUTH)
                }
            }

            JUMP_TO_SIGN_UP -> {

                supportFragmentManager.popBackStack(
                    BACK_STACK_AUTH,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )

                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.main, SignUpFragment::class.java, null)
                    addToBackStack(BACK_STACK_AUTH)
                }
            }

            JUMP_TO_HOME -> {

                supportFragmentManager.popBackStack(
                    BACK_STACK_AUTH,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(R.id.main, HomeFragment::class.java, args)
                    addToBackStack(BACK_STACK_ONBOARD)
                }
            }

            else -> debugging("Navigate Error")
        }
    }

    fun showSnackBar (message : String ){
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_LONG).show()
    }

    fun showToast (message : String ){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}