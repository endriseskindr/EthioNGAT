package com.ngat.ethiopia.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vocabulary",
    foreignKeys = [
        ForeignKey(
            entity = Cluster::class,
            parentColumns = ["id"],
            childColumns = ["cluster_id"]
        )
    ],
    indices = [
        Index(value = ["word"], name = "idx_vocabulary_word"),
        Index(value = ["cluster_id"], name = "idx_vocabulary_cluster")
    ]
)
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
