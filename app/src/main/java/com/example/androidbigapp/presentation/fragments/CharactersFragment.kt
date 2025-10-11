package com.example.androidbigapp.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidbigapp.R
import com.example.androidbigapp.data.CharacterResponse
import com.example.androidbigapp.presentation.SingleActivity
import com.example.androidbigapp.databinding.FragmentCharactersBinding
import com.example.androidbigapp.network.RetrofitNetwork
import com.example.androidbigapp.network.RetrofitNetworkApi
import com.example.androidbigapp.presentation.ApiResponseAdapter
import com.example.androidbigapp.presentation.extensions.debugging
import kotlinx.coroutines.launch
import kotlin.getValue

class CharactersFragment: Fragment() {
    private lateinit var binding: FragmentCharactersBinding
    private var _retrofitApi: RetrofitNetworkApi? = null
    private val retrofitApi get() = _retrofitApi!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCharactersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.debugging("CharactersFragment - onViewCreated")

        _retrofitApi = RetrofitNetwork()
        binding.charactersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ApiResponseAdapter(emptyList())
        binding.charactersRecyclerView.adapter = adapter

        val args: CharactersFragmentArgs by navArgs()
        val house = args.HOUSE

        lifecycleScope.launch {
            try {
                val characters = when (house) {
                    1 -> retrofitApi.getCharactersByHouseName("Stark")
                    2 -> retrofitApi.getCharactersByHouseName("Lannister")
                    3 -> retrofitApi.getCharactersByHouseName("Baratheon")
                    4 -> retrofitApi.getCharactersByHouseName("Targaryen")
                    else -> emptyList()
                }

                Log.d("API", characters.toString())
                adapter.setData(characters)

            } catch (e: Exception) {
                (activity as SingleActivity).showToast("Error: ${e.message}")
                Log.e("API", "Error", e)
            }
        }
    }
}