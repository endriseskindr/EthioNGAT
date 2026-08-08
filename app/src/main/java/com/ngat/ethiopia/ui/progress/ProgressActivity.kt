package com.ngat.ethiopia.ui.progress

import android.app.AlertDialog
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ngat.ethiopia.NigatApp
import com.ngat.ethiopia.databinding.ActivityProgressBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBinding
    private val repository by lazy { (application as NigatApp).repository }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Progress"

        loadStats()
        setupResetButton()
    }

    private fun loadStats() {
        lifecycleScope.launch {
            val totalQ = repository.getTotalQuestions()
            val totalV = repository.getVocabularyCount()
            val now = System.currentTimeMillis()
            val reviewedQ = repository.getReviewedCount("question")
            val reviewedV = repository.getReviewedCount("vocabulary")
            val dueQ = repository.getDueCount("question", now)
            val dueV = repository.getDueCount("vocabulary", now)

            // Overall
            val totalItems = totalQ + totalV
            val reviewedItems = reviewedQ + reviewedV
            val overallPct = if (totalItems > 0) (reviewedItems * 100) / totalItems else 0
            binding.progressOverall.progress = overallPct
            binding.tvOverallPct.text = "$overallPct%"
            binding.tvOverallDesc.text = "$reviewedItems of $totalItems items practiced"

            // Questions
            val qPct = if (totalQ > 0) (reviewedQ * 100) / totalQ else 0
            binding.progressQuestions.progress = qPct
            binding.tvQuestionsPct.text = "$qPct%"
            binding.tvQuestionsDesc.text = "$reviewedQ / $totalQ questions"

            // Vocabulary
            val vPct = if (totalV > 0) (reviewedV * 100) / totalV else 0
            binding.progressVocabulary.progress = vPct
            binding.tvVocabularyPct.text = "$vPct%"
            binding.tvVocabularyDesc.text = "$reviewedV / $totalV words"

            // Due today
            binding.tvDueQuestions.text = "$dueQ questions due"
            binding.tvDueVocabulary.text = "$dueV words due"
            binding.tvDueTotal.text = "${dueQ + dueV} items"
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.getRecentReviews(20).collectLatest { reviews ->
                    val last7 = reviews.count {
                        val weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
                        (it.last_reviewed_at ?: 0) > weekAgo
                    }
                    val mastered = reviews.count { (it.last_quality ?: 0) >= 4 }
                    val struggled = reviews.count { (it.last_quality ?: 0) < 3 }

                    binding.tvLast7Days.text = "$last7 sessions"
                    binding.tvMastered.text = "$mastered items"
                    binding.tvStruggled.text = "$struggled items"
                }
            }
        }
    }

    private fun setupResetButton() {
        binding.btnReset.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Reset All Progress")
                .setMessage("This will permanently delete all your study progress. This cannot be undone.")
                .setPositiveButton("Reset") { _, _ ->
                    lifecycleScope.launch {
                        repository.clearAllProgress()
                        loadStats()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
