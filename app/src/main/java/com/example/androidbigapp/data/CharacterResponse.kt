package com.example.androidbigapp.data

import kotlinx.serialization.Serializable

@Serializable
data class CharacterResponse(
    val characterName: String? = null,
    val houseName: List<String>? = null,
    val houseNames: List<String>? = null,
    val gender: String? = null,
    val culture: String? = null,
    val born: String? = null,
    val died: String? = null,
    val titles: List<String>? = null,
    val aliases: List<String>? = null,
    val father: String? = null,
    val mother: String? = null,
    val spouse: String? = null,
    val allegiances: List<String>? = null,
    val books: List<String>? = null,
    val povBooks: List<String>? = null,
    val tvSeries: List<String>? = null,
    val playedBy: List<String>? = null,
    val characterImageThumb: String? = null,
    val characterImageFull: String? = null,
    val characterLink: String? = null,
    val actors: List<Actor>? = null,
    val parents: List<String>? = null,
    val parentOf: List<String>? = null,
    val siblings: List<String>? = null,
    val sibling: List<String>? = null,
    val killed: List<String>? = null,
    val killedBy: List<String>? = null,
    val serves: List<String>? = null,
    val servedBy: List<String>? = null,
    val guardedBy: List<String>? = null,
    val guardianOf: List<String>? = null,
    val allies: List<String>? = null,
    val marriedEngaged: List<String>? = null,
    val abductedBy: List<String>? = null,
    val abducted: List<String>? = null,
    val royal: Boolean? = null,
    val kingsguard: Boolean? = null,
    val nickname: String? = null,
    val actorName: String? = null,
    val actorLink: String? = null
) {
    @Serializable
    data class Actor(
        val actorName: String? = null,
        val actorLink: String? = null,
        val seasonsActive: List<Int>? = null
    )
}