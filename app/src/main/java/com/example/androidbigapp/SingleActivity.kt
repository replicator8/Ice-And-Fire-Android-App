package com.example.androidbigapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.add
import com.example.androidbigapp.extensions.debugging
import com.example.androidbigapp.fragments.OnboardFragment
import com.example.androidbigapp.fragments.SignInFragment
import com.example.androidbigapp.fragments.SignUpFragment
import com.google.android.material.snackbar.Snackbar
import com.example.androidbigapp.SingleActivity
import com.example.androidbigapp.fragments.HomeFragment

class SingleActivity: AppCompatActivity(R.layout.activity) {
    companion object {
        const val JUMP_TO_SIGN_IN = 1
        const val JUMP_FROM_RETURNING = 2
        const val JUMP_TO_SIGN_UP = 3

        const val JUMP_TO_HOME = 4

        const val ADMIN: String = "ADMIN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        debugging("SingleActivity - onCreate")

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<OnboardFragment>(R.id.main)
            }
        }
    }

    fun navigate (jump : Int, args: Bundle? = null) : Unit {
        when (jump) {
            JUMP_TO_SIGN_IN -> {
                debugging("Open fragment to Sign In")

                supportFragmentManager.commit {
                    replace(R.id.main, SignInFragment::class.java, args)
                    setReorderingAllowed(true)
                    addToBackStack("start")
                }
            }
            JUMP_TO_SIGN_UP -> {
                debugging("Open fragment to Sign Up")

                supportFragmentManager.commit {
                    replace(R.id.main, SignUpFragment::class.java, null)
                    setReorderingAllowed(true)
                    addToBackStack("start")
                }
            }

            JUMP_TO_HOME -> {
                debugging("Open fragment to Home")

                supportFragmentManager.commit {
                    replace(R.id.main, HomeFragment::class.java, args)
                    setReorderingAllowed(true)
                    addToBackStack("start")
                }
            }
            else ->{debugging("Navigate Error")}
        }
    }

    fun showSnackBar (message : String ){
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_LONG).show()
    }

    fun showToast (message : String ){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}