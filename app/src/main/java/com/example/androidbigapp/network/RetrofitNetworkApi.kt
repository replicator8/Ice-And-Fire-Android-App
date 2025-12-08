package com.example.androidbigapp.network

import com.example.androidbigapp.data.CharacterResponse
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitNetworkApi {
    @GET(value = "characters")
    suspend fun getAllCharacters(): List<CharacterResponse>?

    @GET("characters/{houseName}")
    suspend fun getCharactersPaged(@Path("houseName") houseName: String, @Query("page") page: Int, @Query("size") size: Int): PagedResponse<CharacterResponse>
}

private const val NETWORK_BASE_URL = "http://10.245.69.208:8098/api/got/" // ipconfig getifaddr en0  172.20.10.5

class RetrofitNetwork: RetrofitNetworkApi {

    private val networkApi: RetrofitNetworkApi by lazy {2
        Retrofit.Builder()
            .baseUrl(NETWORK_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitNetworkApi::class.java)
    }

    override suspend fun getAllCharacters(): List<CharacterResponse>? = networkApi.getAllCharacters()
    override suspend fun getCharactersPaged(houseName: String, page: Int, size: Int): PagedResponse<CharacterResponse> = networkApi.getCharactersPaged(houseName, page, size)
}

data class PagedResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val hasNext: Boolean
)