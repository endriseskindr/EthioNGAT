package com.ngat.ethiopia.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary")
data class Vocabulary(
    @PrimaryKey val id: String,
    val word: String,
    val pos: String,
    val definition: String,
    val example: String,
    val cluster_id: Int
) {
    fun getPosFull(): String = when (pos.uppercase()) {
        "N" -> "Noun"
        "V" -> "Verb"
        "ADJ" -> "Adjective"
        "ADV" -> "Adverb"
        "PREP" -> "Preposition"
        else -> pos
    }
}
