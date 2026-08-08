package com.ngat.ethiopia.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.ngat.ethiopia.data.entity.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions WHERE chapter_id = :chapterId ORDER BY id ASC")
    fun getQuestionsByChapter(chapterId: Int): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE chapter_id = :chapterId ORDER BY id ASC")
    suspend fun getQuestionsByChapterList(chapterId: Int): List<Question>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: String): Question?

    @Query("SELECT * FROM questions WHERE is_trap = 1 ORDER BY chapter_id ASC, id ASC")
    fun getTrapQuestions(): Flow<List<Question>>

    @Query("SELECT COUNT(*) FROM questions WHERE is_trap = 1")
    suspend fun getTrapQuestionCount(): Int

    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestions(limit: Int): List<Question>

    @Query("SELECT * FROM questions WHERE chapter_id = :chapterId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestionsByChapter(chapterId: Int, limit: Int): List<Question>

    @Query("SELECT COUNT(*) FROM questions WHERE chapter_id = :chapterId")
    suspend fun getQuestionCountByChapter(chapterId: Int): Int
}
