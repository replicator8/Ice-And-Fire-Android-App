package com.example.androidbigapp.network

import com.example.androidbigapp.data.CharacterResponse
import retrofit2.converter.gson.GsonConverterFactory;
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitNetworkApi {
    @GET(value = "characters")
    suspend fun getAllCharacters(): List<CharacterResponse>?

    @GET("characters/{houseName}")
    suspend fun getCharactersByHouseName(@Path("houseName") houseName: String): List<CharacterResponse>
}

private const val NETWORK_BASE_URL = "http://172.20.10.5:8098/api/got/" // ipconfig getifaddr en0

class RetrofitNetwork: RetrofitNetworkApi {

    private val networkApi: RetrofitNetworkApi by lazy {2
        Retrofit.Builder()
            .baseUrl(NETWORK_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitNetworkApi::class.java)
    }

    override suspend fun getAllCharacters(): List<CharacterResponse>? = networkApi.getAllCharacters()
    override suspend fun getCharactersByHouseName(houseName: String): List<CharacterResponse> = networkApi.getCharactersByHouseName(houseName)
}