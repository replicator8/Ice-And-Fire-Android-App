package com.example.androidbigapp.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.androidbigapp.R
import com.example.androidbigapp.SingleActivity
import com.example.androidbigapp.databinding.FragmentCharactersBinding
import com.example.androidbigapp.extensions.debugging
import kotlin.getValue

class CharactersFragment: Fragment(R.layout.fragment_characters) {
    private var _binding: FragmentCharactersBinding? = null
    private val binding: FragmentCharactersBinding
        get() = _binding ?: throw RuntimeException()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.debugging("CharactersFragment - onViewCreated")

        val args: CharactersFragmentArgs by navArgs()
        val house = args.HOUSE

        when (house) {
            1 -> (activity as SingleActivity).showToast("HOUSE STARK")
            2 -> (activity as SingleActivity).showToast("HOUSE LANNISTER")
            3 -> (activity as SingleActivity).showToast("HOUSE BARATHEON")
            4 -> (activity as SingleActivity).showToast("HOUSE TARGARYEN")
        }
    }
}