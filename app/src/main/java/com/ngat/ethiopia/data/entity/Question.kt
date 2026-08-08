package com.ngat.ethiopia.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = Chapter::class,
            parentColumns = ["id"],
            childColumns = ["chapter_id"]
        )
    ],
    indices = [
        Index(value = ["is_trap"], name = "idx_questions_is_trap"),
        Index(value = ["chapter_id"], name = "idx_questions_chapter")
    ]
)
data class Question(
    @PrimaryKey val id: String,
    val chapter_id: Int,
    val question_text: String,
    val option_a: String,
    val option_b: String,
    val option_c: String,
    val option_d: String,
    val option_e: String?,
    val answer_key: String,
    val explanation: String,
    val is_trap: Int
) {
    fun isTrapQuestion(): Boolean = is_trap == 1

    fun getOptionByKey(key: String): String? = when (key.uppercase()) {
        "A" -> option_a
        "B" -> option_b
        "C" -> option_c
        "D" -> option_d
        "E" -> option_e
        else -> null
    }

    fun getAllOptions(): List<Pair<String, String>> {
        val list = mutableListOf(
            "A" to option_a,
            "B" to option_b,
            "C" to option_c,
            "D" to option_d
        )
        option_e?.let { if (it.isNotBlank()) list.add("E" to it) }
        return list
    }
}
