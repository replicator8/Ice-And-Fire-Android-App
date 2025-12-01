package com.example.androidbigapp.data

import kotlinx.serialization.Serializable

@Serializable
data class CharacterResponse(
    val characterName: String? = null,
    val houseName: List<String>? = null,
    val gender: String? = null,
    val parents: List<String>? = null,
    val actorName: String? = null,
)