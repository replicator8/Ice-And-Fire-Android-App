package com.example.androidbigapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.androidbigapp.entities.Character
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters")
    fun getAllLiveData(): LiveData<List<Character>>

    @Query("SELECT * FROM characters")
    fun getAll(): List<Character>

    @Query("SELECT * FROM characters WHERE character_name = :name")
    fun getCharacterByName(name: String): Character?

    @Query("SELECT * FROM characters WHERE house_name = :houseName")
    fun getCharactersByHouse(houseName: String): List<Character>?

    @Query("SELECT COUNT(*) FROM characters")
    fun getCount(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM characters WHERE house_name = :house)")
    suspend fun hasCharactersFromHouse(house: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(character: Character)

    @Update
    suspend fun update(character: Character)

    @Insert
    suspend fun insertAll(characters: List<Character>)

    @Delete
    suspend fun delete(character: Character)

    @Query("DELETE FROM characters WHERE house_name = :houseName")
    suspend fun deleteByHouse(houseName: String)

    @Query("SELECT * FROM characters WHERE house_name = :houseName")
    fun observeCharactersByHouse(houseName: String): Flow<List<Character>>
}