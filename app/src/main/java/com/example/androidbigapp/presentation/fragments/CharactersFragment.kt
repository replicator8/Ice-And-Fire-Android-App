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
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidbigapp.data.BACKUP_FILENAME_KEY
import com.example.androidbigapp.data.CharacterResponse
import com.example.androidbigapp.data.FILE_EXISTS_KEY
import com.example.androidbigapp.data.FILE_SIZE_KEY
import com.example.androidbigapp.data.dataStore
import com.example.androidbigapp.presentation.SingleActivity
import com.example.androidbigapp.databinding.FragmentCharactersBinding
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

                if (house == 1 && characters.isNotEmpty()) {
                    saveStarkBackup(characters)
                }

                Log.d("API", characters.toString())
                adapter.setData(characters)

            } catch (e: Exception) {
                (activity as SingleActivity).showToast("Error: ${e.message}")
                Log.e("API", "Error", e)
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