package com.ngat.ethiopia.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ngat.ethiopia.NigatApp
import com.ngat.ethiopia.databinding.ActivityHomeBinding
import com.ngat.ethiopia.ui.chapters.ChaptersActivity
import com.ngat.ethiopia.ui.progress.ProgressActivity
import com.ngat.ethiopia.ui.quiz.QuizActivity
import com.ngat.ethiopia.ui.vocabulary.VocabularyActivity
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val repository by lazy { (application as NigatApp).repository }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClicks()
        loadStats()
    }

    private fun setupClicks() {
        binding.cardChapters.setOnClickListener {
            startActivity(Intent(this, ChaptersActivity::class.java))
        }
        binding.cardVocabulary.setOnClickListener {
            startActivity(Intent(this, VocabularyActivity::class.java))
        }
        binding.cardProgress.setOnClickListener {
            startActivity(Intent(this, ProgressActivity::class.java))
        }
        binding.cardQuickQuiz.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra(QuizActivity.EXTRA_MODE, QuizActivity.MODE_RANDOM)
                putExtra(QuizActivity.EXTRA_QUIZ_SIZE, 20)
            }
            startActivity(intent)
        }
        binding.cardTrapQuestions.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra(QuizActivity.EXTRA_MODE, QuizActivity.MODE_TRAP)
            }
            startActivity(intent)
        }
    }

    private fun loadStats() {
        lifecycleScope.launch {
            val totalQ = repository.getTotalQuestions()
            val totalV = repository.getVocabularyCount()
            val trapCount = repository.getTrapCount()
            val now = System.currentTimeMillis()
            val dueQ = repository.getDueCount("question", now)
            val dueV = repository.getDueCount("vocabulary", now)

            binding.tvTotalQuestions.text = "$totalQ Questions"
            binding.tvTotalVocab.text = "$totalV Words"
            binding.tvTrapCount.text = "$trapCount Trap Questions"
            binding.tvDueToday.text = "${dueQ + dueV} Due Today"
        }
    }

    override fun onResume() {
        super.onResume()
        loadStats()
    }
}
