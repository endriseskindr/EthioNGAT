package com.ngat.ethiopia.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ngat.ethiopia.data.entity.UserProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: UserProgress)

    @Update
    suspend fun update(progress: UserProgress)

    @Query("SELECT * FROM user_progress WHERE item_type = :type AND item_id = :id")
    suspend fun getProgress(type: String, id: String): UserProgress?

    @Query("SELECT * FROM user_progress WHERE item_type = :type AND (due_at IS NULL OR due_at <= :now) ORDER BY due_at ASC NULLS FIRST")
    fun getDueItems(type: String, now: Long): Flow<List<UserProgress>>

    @Query("SELECT COUNT(*) FROM user_progress WHERE item_type = :type AND (due_at IS NULL OR due_at <= :now)")
    suspend fun getDueCount(type: String, now: Long): Int

    @Query("SELECT COUNT(*) FROM user_progress WHERE item_type = :type")
    suspend fun getReviewedCount(type: String): Int

    @Query("SELECT * FROM user_progress WHERE last_quality IS NOT NULL ORDER BY last_reviewed_at DESC LIMIT :limit")
    fun getRecentReviews(limit: Int): Flow<List<UserProgress>>

    @Query("DELETE FROM user_progress")
    suspend fun clearAllProgress()
}
