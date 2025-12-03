package com.example.androidbigapp

import android.app.Application
import androidx.room.Room
import com.example.androidbigapp.database.CharacterDatabase

class App : Application() {
    private lateinit var db: CharacterDatabase

    override fun onCreate() {
        super.onCreate()

        this.db = Room.databaseBuilder(
            this,
            CharacterDatabase::class.java, "db-characters"
        ).build()
    }

    fun getDb(): CharacterDatabase {
        return db
    }
}