package com.ngat.ethiopia.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_progress",
    primaryKeys = ["item_type", "item_id"],
    indices = [
        Index(value = ["due_at"], name = "idx_progress_due")
    ]
)
data class UserProgress(
    val item_type: String,
    val item_id: String,
    val repetitions: Int = 0,
    val ease_factor: Double = 2.5,
    val interval_days: Int = 0,
    val due_at: Long? = null,
    val last_reviewed_at: Long? = null,
    val last_quality: Int? = null
)
