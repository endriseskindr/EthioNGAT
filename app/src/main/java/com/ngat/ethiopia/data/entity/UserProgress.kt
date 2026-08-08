package com.ngat.ethiopia.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress", primaryKeys = ["item_type", "item_id"])
data class UserProgress(
    val item_type: String,       // "question" or "vocabulary"
    val item_id: String,
    val repetitions: Int = 0,
    val ease_factor: Double = 2.5,
    val interval_days: Int = 0,
    val due_at: Long? = null,           // epoch millis
    val last_reviewed_at: Long? = null, // epoch millis
    val last_quality: Int? = null       // 0..5 SM-2 grade
)
