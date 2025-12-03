package com.example.androidbigapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidbigapp.entities.Character

@Database(entities = [Character::class], version = 1)
abstract class CharacterDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
}