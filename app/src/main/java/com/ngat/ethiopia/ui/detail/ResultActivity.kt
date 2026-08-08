package com.ngat.ethiopia.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.ngat.ethiopia.databinding.ActivityResultBinding
import com.ngat.ethiopia.ui.home.HomeActivity

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    companion object {
        const val EXTRA_SCORE = "extra_score"
        const val EXTRA_TOTAL = "extra_total"
        const val EXTRA_CHAPTER_NAME = "extra_chapter_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Quiz Complete"

        val score = intent.getIntExtra(EXTRA_SCORE, 0)
        val total = intent.getIntExtra(EXTRA_TOTAL, 0)
        val chapterName = intent.getStringExtra(EXTRA_CHAPTER_NAME) ?: ""

        displayResult(score, total, chapterName)

        binding.btnHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        binding.btnRetry.setOnClickListener { finish() }
    }

    private fun displayResult(score: Int, total: Int, chapterName: String) {
        val percentage = if (total > 0) (score * 100) / total else 0

        binding.tvScore.text = "$score / $total"
        binding.tvPercentage.text = "$percentage%"
        binding.progressScore.progress = percentage
        binding.tvChapterName.text = chapterName.ifEmpty { "Quiz" }

        val (message, color) = when {
            percentage >= 85 -> "Excellent Work!" to "#2E7D32"
            percentage >= 70 -> "Great Job!" to "#388E3C"
            percentage >= 50 -> "Keep Practicing" to "#F57C00"
            else -> "Review and Try Again" to "#C62828"
        }

        binding.tvMessage.text = message
        binding.tvMessage.setTextColor(android.graphics.Color.parseColor(color))

        val correct = score
        val wrong = total - score
        binding.tvCorrect.text = "$correct Correct"
        binding.tvWrong.text = "$wrong Incorrect"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
