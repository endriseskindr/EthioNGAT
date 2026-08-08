package com.ngat.ethiopia

import android.app.Application
import com.ngat.ethiopia.data.NigatDatabase
import com.ngat.ethiopia.data.NigatRepository

class NigatApp : Application() {

    lateinit var repository: NigatRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = NigatDatabase.getInstance(this)
        repository = NigatRepository(
            chapterDao = db.chapterDao(),
            questionDao = db.questionDao(),
            vocabularyDao = db.vocabularyDao(),
            progressDao = db.userProgressDao()
        )
        instance = this
    }

    companion object {
        lateinit var instance: NigatApp
            private set
    }
}
