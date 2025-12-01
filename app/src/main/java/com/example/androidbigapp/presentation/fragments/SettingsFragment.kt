package com.example.androidbigapp.presentation.fragments

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.androidbigapp.R
import com.example.androidbigapp.data.BACKUP_FILENAME_KEY
import com.example.androidbigapp.data.FILE_EXISTS_KEY
import com.example.androidbigapp.data.FILE_SIZE_KEY
import com.example.androidbigapp.data.INTERNAL_BACKUP_EXISTS_KEY
import com.example.androidbigapp.data.NOTIFICATIONS_ENABLED_KEY
import com.example.androidbigapp.data.dataStore
import com.example.androidbigapp.databinding.FragmentSettingsBinding
import com.example.androidbigapp.presentation.SingleActivity
import com.example.androidbigapp.presentation.extensions.debugging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class SettingsFragment: Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding ?: throw RuntimeException()

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = requireContext().getSharedPreferences("user_settings", Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.debugging("SettingsFragment - onViewCreated")
        _binding = FragmentSettingsBinding.bind(view)

        setThemeRadioButton()
        setLangRadioButton()
        loadNotificationsSetting()
        loadFileName()

        lifecycleScope.launch {
            requireContext().dataStore.data
                .map { prefs ->
                    BackupStatus(
                        filename = prefs[BACKUP_FILENAME_KEY] ?: "backup_stark.txt",
                        exists = prefs[FILE_EXISTS_KEY] ?: false,
                        size = prefs[FILE_SIZE_KEY] ?: 0L,
                        internalBackupExists = prefs[INTERNAL_BACKUP_EXISTS_KEY] ?: false
                    )
                }
                .collect { status ->
                    updateBackupInfoUi(status)
                }
        }

        with(binding) {
            var theme = ""
            var language = ""

            themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                if (R.id.light_theme_radio == checkedId) {
                    theme = "light"
                } else if (R.id.dark_theme_radio == checkedId) {
                    theme = "dark"
                }

                prefs.edit().putString("theme", theme).apply()
                activity?.debugging("Saved theme to Shared Preferencies")
            }

            languageRadioGroup.setOnCheckedChangeListener { _,  checkedId ->
                if (R.id.ru_lang_radio == checkedId) {
                    language = "ru"
                } else if (R.id.en_lang_radio == checkedId) {
                    language = "en"
                }

                prefs.edit().putString("language", language).apply()

                setAppLanguage(language)
                activity?.debugging("Saved language to Shared Preferencies")
            }

            notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
                lifecycleScope.launch {
                    requireContext().dataStore.edit { settings ->
                        settings[NOTIFICATIONS_ENABLED_KEY] = isChecked
                    }
                }
            }

            binding.backupFilenameEdit.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val newName = binding.backupFilenameEdit.text.toString().trim()
                    if (newName.isNotEmpty()) {
                        val finalName = if (!newName.endsWith(".txt")) "$newName.txt" else newName
                        lifecycleScope.launch {
                            requireContext().dataStore.edit { settings ->
                                settings[BACKUP_FILENAME_KEY] = finalName
                            }
                        }
                    }
                    hideKeyboard()
                    true
                } else {
                    false
                }
            }

            btnDeleteFile.setOnClickListener {
                lifecycleScope.launch {
                    deleteExternalFileAndSaveInternalBackup()
                }
            }

            binding.btnRestoreBackup.setOnClickListener {
                lifecycleScope.launch {
                    restoreInternalBackupToExternalStorage()
                }
            }

        }
    }

    private suspend fun deleteExternalFileAndSaveInternalBackup() {
        try {
            val prefs = requireContext().dataStore.data.first()
            val filename = prefs[BACKUP_FILENAME_KEY] ?: "starks.txt"

            val fileUri = findFileInDocuments(requireContext(), filename)
            if (fileUri == null) {
                withContext(Dispatchers.Main) {
                    (activity as? SingleActivity)?.showToast("Файл не найден")
                }
                return
            }

            val content = withContext(Dispatchers.IO) {
                requireContext().contentResolver.openInputStream(fileUri)?.use { it.readBytes() }
            } ?: run {
                withContext(Dispatchers.Main) {
                    (activity as? SingleActivity)?.showToast("Не удалось прочитать файл")
                }
                return
            }

            withContext(Dispatchers.IO) {
                requireContext().openFileOutput(filename, Context.MODE_PRIVATE).use { out ->
                    out.write(content)
                }
            }

            val deleted = withContext(Dispatchers.IO) {
                requireContext().contentResolver.delete(fileUri, null, null) > 0
            }

            if (deleted) {
                requireContext().dataStore.edit { settings ->
                    settings[FILE_EXISTS_KEY] = false
                    settings[INTERNAL_BACKUP_EXISTS_KEY] = true
                }
                withContext(Dispatchers.Main) {
                    (activity as? SingleActivity)?.showToast("Файл удалён, резервная копия сохранена")
                }
            } else {
                throw IOException("Не удалось удалить файл")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                (activity as? SingleActivity)?.showToast("Ошибка при удалении: ${e.message}")
            }
        }
    }

    private suspend fun restoreInternalBackupToExternalStorage() {
        try {
            val prefs = requireContext().dataStore.data.first()
            val filename = prefs[BACKUP_FILENAME_KEY] ?: "starks.txt"
            val internalBackupExists = prefs[INTERNAL_BACKUP_EXISTS_KEY] ?: false

            if (!internalBackupExists) {
                withContext(Dispatchers.Main) {
                    (activity as? SingleActivity)?.showToast("Резервная копия не найдена")
                }
                return
            }

            val content = withContext(Dispatchers.IO) {
                requireContext().openFileInput(filename).readBytes()
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
                        stream.write(content)
                    }

                    val fileSize = content.size.toLong()
                    requireContext().dataStore.edit { settings ->
                        settings[FILE_EXISTS_KEY] = true
                        settings[FILE_SIZE_KEY] = fileSize
                        settings[INTERNAL_BACKUP_EXISTS_KEY] = false
                    }

                    withContext(Dispatchers.Main) {
                        (activity as? SingleActivity)?.showToast("Резервная копия восстановлена")
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                (activity as? SingleActivity)?.showToast("Ошибка при восстановлении: ${e.message}")
            }
        }
    }

    private fun setLangRadioButton() {
        val currentLang = prefs.getString("language", "en")
        activity?.debugging("SettingsFragment: Loaded language from prefs: $currentLang")

        if (currentLang.equals("ru")) {
            binding.enLangRadio.isChecked = false
            binding.ruLangRadio.isChecked = true
        } else {
            binding.enLangRadio.isChecked = true
            binding.ruLangRadio.isChecked = false
        }
    }

    private fun setThemeRadioButton() {
        val currentTheme = prefs.getString("theme", "dark")
        activity?.debugging("SettingsFragment: Loaded theme from prefs: $currentTheme")

        if (currentTheme.equals("dark")) {
            binding.lightThemeRadio.isChecked = false
            binding.darkThemeRadio.isChecked = true
        } else {
            binding.lightThemeRadio.isChecked = true
            binding.darkThemeRadio.isChecked = false
        }
    }

    fun setAppLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        requireActivity().baseContext.resources.updateConfiguration(
            config,
            requireActivity().baseContext.resources.displayMetrics
        )

        requireActivity().recreate()
    }

    private fun loadNotificationsSetting() {
        lifecycleScope.launch {
            requireContext().dataStore.data.collect { preferences ->
                val isEnabled = preferences[NOTIFICATIONS_ENABLED_KEY] ?: true
                binding.notificationsSwitch.isChecked = isEnabled
            }
        }
    }

    private fun loadFileName() {
        lifecycleScope.launch {
            val prefs = requireContext().dataStore.data.first()
            val filename = prefs[BACKUP_FILENAME_KEY] ?: "backup_characters.txt"
            binding.backupFilenameEdit.setText(filename)
        }
    }

    private fun updateBackupInfoUi(status: BackupStatus) {
        if (status.exists) {
            binding.tvBackupStatus.text = getString(R.string.backup_found)
            binding.tvBackupStatus.setTextColor(Color.GREEN)
            binding.tvBackupFilename.text = "Имя: ${status.filename}"
            binding.tvBackupSize.text = "Размер: ${formatFileSize(status.size)}"
        } else {
            binding.tvBackupStatus.text = getString(R.string.backup_not_found)
            binding.tvBackupStatus.setTextColor(Color.RED)
            binding.tvBackupFilename.text = ""
            binding.tvBackupSize.text = ""
        }

        if (status.internalBackupExists) {
            binding.tvInternalBackupStatus.text = getString(R.string.internal_backup_saved)
            binding.tvInternalBackupStatus.setTextColor(Color.GREEN)
        } else {
            binding.tvInternalBackupStatus.text = getString(R.string.internal_backup_not_saved)
            binding.tvInternalBackupStatus.setTextColor(Color.RED)
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.backupFilenameEdit.windowToken, 0)
    }

    private suspend fun findFileInDocuments(context: Context, displayName: String): Uri? {
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
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                    MediaStore.Files.getContentUri("external", id)
                } else null
            }
        }
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1_000_000 -> String.format("%.2f MB", bytes / 1_000_000.0)
            bytes >= 1_000 -> String.format("%.2f KB", bytes / 1_000.0)
            else -> "$bytes B"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private data class BackupStatus(
    val filename: String,
    val exists: Boolean,
    val size: Long,
    val internalBackupExists: Boolean
)