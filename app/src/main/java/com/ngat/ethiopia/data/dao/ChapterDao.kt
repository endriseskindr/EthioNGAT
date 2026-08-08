package com.ngat.ethiopia.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.ngat.ethiopia.data.entity.Chapter
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {

    @Query("SELECT * FROM chapters ORDER BY id ASC")
    fun getAllChapters(): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE id = :id")
    suspend fun getChapterById(id: Int): Chapter?

    @Query("SELECT DISTINCT section FROM chapters ORDER BY id ASC")
    suspend fun getDistinctSections(): List<String>

    @Query("SELECT * FROM chapters WHERE section = :section ORDER BY id ASC")
    fun getChaptersBySection(section: String): Flow<List<Chapter>>

    @Query("SELECT SUM(item_count) FROM chapters")
    suspend fun getTotalQuestions(): Int
}
