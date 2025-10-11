package com.example.androidbigapp.presentation.extensions

import android.util.Log
import androidx.fragment.app.Fragment

fun Fragment.debugging(message: String) {
    if (DEBUG) Log.d("${TAG}_${javaClass.simpleName}", message)
}