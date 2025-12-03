package com.example.androidbigapp.presentation.fragments

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidbigapp.App
import com.example.androidbigapp.data.BACKUP_FILENAME_KEY
import com.example.androidbigapp.data.CharacterResponse
import com.example.androidbigapp.data.FILE_EXISTS_KEY
import com.example.androidbigapp.data.FILE_SIZE_KEY
import com.example.androidbigapp.data.HAS_NEXT_KEY
import com.example.androidbigapp.data.LAST_PAGE_KEY
import com.example.androidbigapp.data.dataStore
import com.example.androidbigapp.presentation.SingleActivity
import com.example.androidbigapp.databinding.FragmentCharactersBinding
import com.example.androidbigapp.entities.Character
import com.example.androidbigapp.entities.toResponse
import com.example.androidbigapp.network.RetrofitNetwork
import com.example.androidbigapp.network.RetrofitNetworkApi
import com.example.androidbigapp.presentation.ApiResponseAdapter
import com.example.androidbigapp.presentation.extensions.debugging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        super.onViewCreated(view, savedInstanceState)
        activity?.debugging("CharactersFragment - onViewCreated")

        _retrofitApi = RetrofitNetwork()
        binding.charactersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ApiResponseAdapter(emptyList())
        binding.charactersRecyclerView.adapter = adapter

        val args: CharactersFragmentArgs by navArgs()
        val house = args.HOUSE

        val houseName = when (house) {
            1 -> "Stark"
            2 -> "Lannister"
            3 -> "Baratheon"
            4 -> "Targaryen"
            else -> {
                Log.w("House", "Invalid house ID: $house")
                return
            }
        }

        val db = (requireContext().applicationContext as App).getDb()
        val PAGE_SIZE = 10

        val lastPageKey = intPreferencesKey("last_page_$houseName")
        val hasNextKey = booleanPreferencesKey("has_next_$houseName")

        var currentPage = 0
        var hasMore = true
        val allCharacters = mutableListOf<CharacterResponse>()

        fun updateLoadMoreButton() {
            binding.btnLoadMore.isEnabled = hasMore
            binding.btnLoadMore.isVisible = hasMore
        }

        suspend fun loadNextPage() {
            if (!hasMore) return

            try {
                val pagedResponse = withContext(Dispatchers.IO) {
                    Log.d("API", "houseName=$houseName page=$currentPage, size=$PAGE_SIZE")
                    retrofitApi.getCharactersPaged(houseName, currentPage, PAGE_SIZE)
                }
                Log.d("API", "Сделали запрос к апи: page=$currentPage, size=$PAGE_SIZE")

                val newItems = pagedResponse.content

                if (newItems.isNotEmpty()) {
                    val entities = newItems.map { Character.from(it) }
                    withContext(Dispatchers.IO) {
                        db.characterDao().insertAll(entities)
                    }
                    Log.d("Cache", "Положили в кеш часть данных")
                }

                allCharacters.addAll(newItems)
                adapter.setData(ArrayList(allCharacters))

                hasMore = pagedResponse.hasNext
                if (hasMore) {
                    currentPage++
                }

                requireContext().dataStore.edit { settings ->
                    settings[lastPageKey] = currentPage
                    settings[hasNextKey] = hasMore
                }
                Log.d("DataStore", "сохранили lastPage & hasNext")

                if (house == 1 && allCharacters.size == newItems.size && newItems.isNotEmpty()) {
                    saveStarkBackup(ArrayList(allCharacters))
                    Log.d("Backup", "Создаем .txt backup со всеми старками")
                }

                updateLoadMoreButton()

            } catch (e: Exception) {
                Log.e("Paging", "Failed to load page $currentPage", e)
                (activity as? SingleActivity)?.showToast("Ошибка загрузки: ${e.message}")
            }
        }

        fun refreshData() {
            allCharacters.clear()
            currentPage = 0
            hasMore = true
            adapter.setData(emptyList())
            updateLoadMoreButton()

            lifecycleScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        db.characterDao().deleteByHouse(houseName)
                        (activity)?.debugging("удаляем кеш дома: $houseName")
                    }

                    requireContext().dataStore.edit { settings ->
                        settings[lastPageKey] = 0
                        settings[hasNextKey] = true
                        (activity)?.debugging("обнуляем data store")
                    }

                    loadNextPage()
                    (activity)?.debugging("Загружаем новые данные...")
                } catch (e: Exception) {
                    Log.e("Refresh", "Failed to refresh", e)
                    (activity as? SingleActivity)?.showToast("Ошибка обновления")
                } finally {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        binding.btnLoadMore.setOnClickListener {
            lifecycleScope.launch {
                loadNextPage()
            }
        }

        lifecycleScope.launch {

            binding.swipeRefreshLayout.setOnRefreshListener {
                refreshData()
            }

            val cachedCharacters = withContext(Dispatchers.IO) {
                db.characterDao().getCharactersByHouse(houseName)?.map { it.toResponse() } ?: emptyList()
            }

            if (cachedCharacters.isNotEmpty()) {
                allCharacters.addAll(cachedCharacters)
                adapter.setData(ArrayList(allCharacters))
                Log.d("Cache", "Показали ${cachedCharacters.size} элементов из кэша")
            }

            val prefs = requireContext().dataStore.data.first()
            currentPage = prefs[lastPageKey] ?: 0
            hasMore = prefs[hasNextKey] ?: true

            if (cachedCharacters.isEmpty()) {
                loadNextPage()
            } else {
                updateLoadMoreButton()
            }
        }
    }

    private suspend fun saveStarkBackup(characters: List<CharacterResponse>) {
        try {
            val prefs = requireContext().dataStore.data.first()
            val filename = prefs[BACKUP_FILENAME_KEY] ?: "backup_stark.txt"

            if (isFileExistsInDocuments(requireContext(), filename)) {
                activity?.debugging("Backup file already exists, skipping creation")
                return
            }

            val content = characters.joinToString("\n\n") { char ->
                """
            Имя: ${char.characterName ?: "—"}
            Дом: ${char.houseName?.joinToString(", ") ?: "—"}
            Пол: ${char.gender ?: "—"}
            Родители: ${char.parents?.joinToString(", ") ?: "—"}
            Актёр: ${char.actorName ?: "—"}
            """.trimIndent()
            }

            withContext(Dispatchers.IO) {
                val resolver = requireContext().contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Files.FileColumns.DISPLAY_NAME, filename)
                    put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                }

                val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                uri?.let {
                    resolver.openOutputStream(it)?.use { stream ->
                        stream.write(content.toByteArray(Charsets.UTF_8))
                    }

                    val fileSize = getFileSize(requireContext(), uri)
                    requireContext().dataStore.edit { settings ->
                        settings[FILE_EXISTS_KEY] = true
                        settings[FILE_SIZE_KEY] = fileSize
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("Backup", "Failed to save Stark backup", e)
        }
    }

    private suspend fun getFileSize(context: Context, uri: Uri): Long {
        return withContext(Dispatchers.IO) {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { fd ->
                fd.statSize
            } ?: 0L
        }
    }

    private suspend fun isFileExistsInDocuments(context: Context, displayName: String): Boolean {
        return withContext(Dispatchers.IO) {
            val projection = arrayOf(MediaStore.Files.FileColumns._ID)
            val selection = "${MediaStore.Files.FileColumns.DISPLAY_NAME} = ? AND ${MediaStore.Files.FileColumns.RELATIVE_PATH} LIKE ?"
            val selectionArgs = arrayOf(displayName, "%${Environment.DIRECTORY_DOCUMENTS}%")

            context.contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                cursor.count > 0
            } ?: false
        }
    }
}