package com.ngat.ethiopia.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ngat.ethiopia.data.dao.ChapterDao
import com.ngat.ethiopia.data.dao.QuestionDao
import com.ngat.ethiopia.data.dao.UserProgressDao
import com.ngat.ethiopia.data.dao.VocabularyDao
import com.ngat.ethiopia.data.entity.Chapter
import com.ngat.ethiopia.data.entity.Cluster
import com.ngat.ethiopia.data.entity.Question
import com.ngat.ethiopia.data.entity.UserProgress
import com.ngat.ethiopia.data.entity.Vocabulary

@Database(
    entities = [
        Chapter::class,
        Question::class,
        Cluster::class,
        Vocabulary::class,
        UserProgress::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NigatDatabase : RoomDatabase() {

    abstract fun chapterDao(): ChapterDao
    abstract fun questionDao(): QuestionDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun userProgressDao(): UserProgressDao

    companion object {
        @Volatile
        private var INSTANCE: NigatDatabase? = null
        private const val DB_NAME = "ngat_seed.db"

        fun getInstance(context: Context): NigatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NigatDatabase::class.java,
                    "nigat_app.db"
                )
                    .createFromAsset(DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
