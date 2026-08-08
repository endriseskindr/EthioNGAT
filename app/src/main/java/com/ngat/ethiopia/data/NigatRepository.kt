package com.ngat.ethiopia.data

import com.ngat.ethiopia.data.dao.ChapterDao
import com.ngat.ethiopia.data.dao.QuestionDao
import com.ngat.ethiopia.data.dao.UserProgressDao
import com.ngat.ethiopia.data.dao.VocabularyDao
import com.ngat.ethiopia.data.entity.Chapter
import com.ngat.ethiopia.data.entity.Cluster
import com.ngat.ethiopia.data.entity.Question
import com.ngat.ethiopia.data.entity.UserProgress
import com.ngat.ethiopia.data.entity.Vocabulary
import com.ngat.ethiopia.util.SM2Algorithm
import kotlinx.coroutines.flow.Flow

class NigatRepository(
    private val chapterDao: ChapterDao,
    private val questionDao: QuestionDao,
    private val vocabularyDao: VocabularyDao,
    private val progressDao: UserProgressDao
) {

    // Chapters
    fun getAllChapters(): Flow<List<Chapter>> = chapterDao.getAllChapters()
    suspend fun getChapter(id: Int): Chapter? = chapterDao.getChapterById(id)
    fun getChaptersBySection(section: String): Flow<List<Chapter>> = chapterDao.getChaptersBySection(section)
    suspend fun getDistinctSections(): List<String> = chapterDao.getDistinctSections()
    suspend fun getTotalQuestions(): Int = chapterDao.getTotalQuestions()

    // Questions
    fun getQuestionsByChapter(chapterId: Int): Flow<List<Question>> = questionDao.getQuestionsByChapter(chapterId)
    suspend fun getQuestionsByChapterList(chapterId: Int): List<Question> = questionDao.getQuestionsByChapterList(chapterId)
    suspend fun getQuestion(id: String): Question? = questionDao.getQuestionById(id)
    fun getTrapQuestions(): Flow<List<Question>> = questionDao.getTrapQuestions()
    suspend fun getTrapCount(): Int = questionDao.getTrapQuestionCount()
    suspend fun getRandomQuestions(limit: Int): List<Question> = questionDao.getRandomQuestions(limit)
    suspend fun getRandomQuestionsByChapter(chapterId: Int, limit: Int): List<Question> =
        questionDao.getRandomQuestionsByChapter(chapterId, limit)

    // Vocabulary
    fun getAllVocabulary(): Flow<List<Vocabulary>> = vocabularyDao.getAllVocabulary()
    suspend fun getAllVocabularyList(): List<Vocabulary> = vocabularyDao.getAllVocabularyList()
    suspend fun getVocab(id: String): Vocabulary? = vocabularyDao.getVocabById(id)
    fun getVocabularyByCluster(clusterId: Int): Flow<List<Vocabulary>> = vocabularyDao.getVocabularyByCluster(clusterId)
    fun searchVocabulary(query: String): Flow<List<Vocabulary>> = vocabularyDao.searchVocabulary(query)
    fun getAllClusters(): Flow<List<Cluster>> = vocabularyDao.getAllClusters()
    suspend fun getCluster(id: Int): Cluster? = vocabularyDao.getClusterById(id)
    suspend fun getVocabularyCount(): Int = vocabularyDao.getVocabularyCount()
    suspend fun getRandomVocabulary(limit: Int): List<Vocabulary> = vocabularyDao.getRandomVocabulary(limit)

    // Progress / SM-2
    suspend fun getProgress(type: String, id: String): UserProgress? = progressDao.getProgress(type, id)
    fun getDueItems(type: String, now: Long): Flow<List<UserProgress>> = progressDao.getDueItems(type, now)
    suspend fun getDueCount(type: String, now: Long): Int = progressDao.getDueCount(type, now)
    suspend fun getReviewedCount(type: String): Int = progressDao.getReviewedCount(type)
    fun getRecentReviews(limit: Int): Flow<List<UserProgress>> = progressDao.getRecentReviews(limit)

    suspend fun recordReview(itemType: String, itemId: String, quality: Int) {
        val current = progressDao.getProgress(itemType, itemId)
        val result = SM2Algorithm.calculateNextReview(current, quality)
        val progress = UserProgress(
            item_type = itemType,
            item_id = itemId,
            repetitions = result.repetitions,
            ease_factor = result.easeFactor,
            interval_days = result.intervalDays,
            due_at = result.dueAt,
            last_reviewed_at = result.lastReviewedAt,
            last_quality = result.lastQuality
        )
        progressDao.insertOrUpdate(progress)
    }

    suspend fun clearAllProgress() = progressDao.clearAllProgress()

    companion object {
        const val TYPE_QUESTION = "question"
        const val TYPE_VOCABULARY = "vocabulary"
    }
}
