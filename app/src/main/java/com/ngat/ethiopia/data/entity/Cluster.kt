package com.ngat.ethiopia.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clusters")
data class Cluster(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val item_count: Int
)
