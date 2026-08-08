package com.ngat.ethiopia.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapters")
data class Chapter(
    @PrimaryKey val id: Int,
    val name: String,
    val section: String,
    val item_count: Int
) {
    fun getSectionColor(): String = when (section) {
        "Quantitative" -> "#1976D2"
        "Analytical Reasoning" -> "#388E3C"
        "Language" -> "#C2185B"
        else -> "#616161"
    }

    fun getSectionIcon(): String = when (section) {
        "Quantitative" -> "∑"
        "Analytical Reasoning" -> "◈"
        "Language" -> "A"
        else -> "?"
    }
}
