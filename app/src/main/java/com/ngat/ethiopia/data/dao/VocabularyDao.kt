package com.ngat.ethiopia.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.ngat.ethiopia.data.entity.Cluster
import com.ngat.ethiopia.data.entity.Vocabulary
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {

    @Query("SELECT * FROM vocabulary ORDER BY word COLLATE NOCASE ASC")
    fun getAllVocabulary(): Flow<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary ORDER BY word COLLATE NOCASE ASC")
    suspend fun getAllVocabularyList(): List<Vocabulary>

    @Query("SELECT * FROM vocabulary WHERE id = :id")
    suspend fun getVocabById(id: String): Vocabulary?

    @Query("SELECT * FROM vocabulary WHERE cluster_id = :clusterId ORDER BY word COLLATE NOCASE ASC")
    fun getVocabularyByCluster(clusterId: Int): Flow<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE word LIKE '%' || :query || '%' OR definition LIKE '%' || :query || '%' ORDER BY word COLLATE NOCASE ASC")
    fun searchVocabulary(query: String): Flow<List<Vocabulary>>

    @Query("SELECT * FROM clusters ORDER BY name ASC")
    fun getAllClusters(): Flow<List<Cluster>>

    @Query("SELECT * FROM clusters WHERE id = :id")
    suspend fun getClusterById(id: Int): Cluster?

    @Query("SELECT COUNT(*) FROM vocabulary")
    suspend fun getVocabularyCount(): Int

    @Query("SELECT * FROM vocabulary ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomVocabulary(limit: Int): List<Vocabulary>
}
