package com.example.androidbigapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidbigapp.data.CharacterResponse

@Entity(tableName = "characters")
data class Character(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "character_name") val characterName: String?,
    @ColumnInfo(name = "house_name") val houseName: String?,
    @ColumnInfo(name = "gender") val gender: String?,
    @ColumnInfo(name = "parent") val parent: String?,
    @ColumnInfo(name = "actor_name") val actorName: String?
) {
    companion object {
        fun from(response: CharacterResponse): Character {
            return Character(
                id = 0,
                characterName = response.characterName,
                houseName = response.houseName?.firstOrNull(),
                gender = response.gender,
                parent = response.parents?.firstOrNull(),
                actorName = response.actorName
            )
        }
    }
}

fun Character.toResponse(): CharacterResponse {
    return CharacterResponse(
        characterName = characterName,
        houseName = listOfNotNull(houseName),
        gender = gender,
        parents = listOfNotNull(parent),
        actorName = actorName
    )
}