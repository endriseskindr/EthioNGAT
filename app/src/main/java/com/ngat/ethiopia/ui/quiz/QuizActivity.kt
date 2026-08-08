package com.ngat.ethiopia.ui.quiz

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ngat.ethiopia.NigatApp
import com.ngat.ethiopia.R
import com.ngat.ethiopia.data.entity.Question
import com.ngat.ethiopia.databinding.ActivityQuizBinding
import com.ngat.ethiopia.ui.detail.ResultActivity
import com.ngat.ethiopia.util.SM2Algorithm
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private val repository by lazy { (application as NigatApp).repository }

    private var questions: List<Question> = emptyList()
    private var currentIndex = 0
    private var selectedAnswer: String? = null
    private var answered = false
    private val userAnswers = mutableMapOf<Int, String?>() // index -> selected key
    private val correctAnswers = mutableListOf<Int>()

    private var mode = MODE_CHAPTER
    private var chapterId = -1
    private var chapterName = ""
    private var quizSize = 0

    companion object {
        const val EXTRA_MODE = "extra_mode"
        const val EXTRA_CHAPTER_ID = "extra_chapter_id"
        const val EXTRA_CHAPTER_NAME = "extra_chapter_name"
        const val EXTRA_QUIZ_SIZE = "extra_quiz_size"

        const val MODE_CHAPTER = "mode_chapter"
        const val MODE_RANDOM = "mode_random"
        const val MODE_TRAP = "mode_trap"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mode = intent.getStringExtra(EXTRA_MODE) ?: MODE_CHAPTER
        chapterId = intent.getIntExtra(EXTRA_CHAPTER_ID, -1)
        chapterName = intent.getStringExtra(EXTRA_CHAPTER_NAME) ?: ""
        quizSize = intent.getIntExtra(EXTRA_QUIZ_SIZE, 0)

        supportActionBar?.title = when (mode) {
            MODE_CHAPTER -> chapterName.ifEmpty { "Chapter Quiz" }
            MODE_RANDOM -> "Quick Quiz ($quizSize Questions)"
            MODE_TRAP -> "Appendix B: Trap Questions"
            else -> "Quiz"
        }

        loadQuestions()
        setupOptionButtons()
        setupNavButtons()
    }

    private fun loadQuestions() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            questions = when (mode) {
                MODE_CHAPTER -> {
                    if (chapterId > 0) repository.getQuestionsByChapterList(chapterId)
                    else emptyList()
                }
                MODE_RANDOM -> repository.getRandomQuestions(quizSize.coerceAtLeast(5))
                MODE_TRAP -> repository.getTrapQuestions().first()
                else -> emptyList()
            }

            binding.progressBar.visibility = View.GONE

            if (questions.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.quizContainer.visibility = View.GONE
            } else {
                binding.emptyState.visibility = View.GONE
                binding.quizContainer.visibility = View.VISIBLE
                showQuestion(0)
            }
        }
    }

    private fun setupOptionButtons() {
        val buttons = listOf(binding.optionA, binding.optionB, binding.optionC, binding.optionD, binding.optionE)
        buttons.forEach { btn ->
            btn.setOnClickListener { onOptionSelected(btn) }
        }
    }

    private fun setupNavButtons() {
        binding.btnNext.setOnClickListener { onNext() }
        binding.btnPrevious.setOnClickListener { onPrevious() }
        binding.btnFinish.setOnClickListener { finishQuiz() }
    }

    private fun onOptionSelected(button: Button) {
        if (answered) return

        val key = when (button.id) {
            R.id.optionA -> "A"
            R.id.optionB -> "B"
            R.id.optionC -> "C"
            R.id.optionD -> "D"
            R.id.optionE -> "E"
            else -> return
        }

        selectedAnswer = key
        resetOptionStyles()
        button.setBackgroundResource(R.drawable.bg_option_selected)
        button.setTextColor(Color.WHITE)
    }

    private fun resetOptionStyles() {
        val buttons = listOf(binding.optionA, binding.optionB, binding.optionC, binding.optionD, binding.optionE)
        buttons.forEach { btn ->
            btn.setBackgroundResource(R.drawable.bg_option_normal)
            btn.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        }
    }

    private fun showQuestion(index: Int) {
        if (index < 0 || index >= questions.size) return

        currentIndex = index
        val q = questions[index]
        answered = userAnswers.containsKey(index)
        selectedAnswer = userAnswers[index]

        binding.tvProgress.text = "${index + 1} / ${questions.size}"
        binding.progressIndicator.progress = ((index + 1) * 100) / questions.size
        binding.tvQuestion.text = q.question_text

        // Trap indicator
        binding.trapBadge.visibility = if (q.isTrapQuestion()) View.VISIBLE else View.GONE

        // Set options
        val buttons = listOf(
            binding.optionA to "A",
            binding.optionB to "B",
            binding.optionC to "C",
            binding.optionD to "D",
            binding.optionE to "E"
        )

        val options = q.getAllOptions().toMap()
        buttons.forEach { (btn, key) ->
            val text = options[key]
            if (text.isNullOrBlank()) {
                btn.visibility = View.GONE
            } else {
                btn.visibility = View.VISIBLE
                btn.text = "$key.  $text"
            }
        }

        resetOptionStyles()

        // Restore previous answer state
        if (answered) {
            showAnswerFeedback(q, userAnswers[index])
        }

        // Nav button states
        binding.btnPrevious.visibility = if (index > 0) View.VISIBLE else View.INVISIBLE
        val isLast = index == questions.size - 1
        binding.btnNext.visibility = if (isLast) View.GONE else View.VISIBLE
        binding.btnFinish.visibility = if (isLast && answered) View.VISIBLE else View.GONE
    }

    private fun showAnswerFeedback(q: Question, selected: String?) {
        answered = true
        val correctKey = q.answer_key.uppercase()
        val buttons = listOf(
            binding.optionA to "A",
            binding.optionB to "B",
            binding.optionC to "C",
            binding.optionD to "D",
            binding.optionE to "E"
        )

        buttons.forEach { (btn, key) ->
            when {
                key == correctKey -> {
                    btn.setBackgroundResource(R.drawable.bg_option_correct)
                    btn.setTextColor(Color.WHITE)
                }
                key == selected && key != correctKey -> {
                    btn.setBackgroundResource(R.drawable.bg_option_wrong)
                    btn.setTextColor(Color.WHITE)
                }
            }
        }

        // Explanation
        binding.explanationCard.visibility = View.VISIBLE
        binding.tvExplanation.text = q.explanation

        // Result icon
        val isCorrect = selected == correctKey
        binding.resultIcon.setImageResource(
            if (isCorrect) R.drawable.ic_check else R.drawable.ic_close
        )
        binding.resultIcon.setColorFilter(
            ContextCompat.getColor(this, if (isCorrect) R.color.correct else R.color.wrong)
        )
        binding.resultText.text = if (isCorrect) "Correct!" else "Incorrect"
        binding.resultText.setTextColor(
            ContextCompat.getColor(this, if (isCorrect) R.color.correct else R.color.wrong)
        )
        binding.resultRow.visibility = View.VISIBLE

        // Record with SM-2
        lifecycleScope.launch {
            val quality = SM2Algorithm.qualityFromCorrectness(isCorrect, if (isCorrect) 4 else 1)
            repository.recordReview("question", q.id, quality)
        }

        val isLast = currentIndex == questions.size - 1
        binding.btnFinish.visibility = if (isLast) View.VISIBLE else View.GONE
    }

    private fun onNext() {
        val q = questions[currentIndex]
        if (!answered && selectedAnswer != null) {
            userAnswers[currentIndex] = selectedAnswer
            val isCorrect = selectedAnswer?.uppercase() == q.answer_key.uppercase()
            if (isCorrect) correctAnswers.add(currentIndex)
            showAnswerFeedback(q, selectedAnswer)
        } else if (!answered) {
            // No answer selected - treat as skipped/wrong
            userAnswers[currentIndex] = null
            showAnswerFeedback(q, null)
        } else {
            // Already answered, move on
            if (currentIndex < questions.size - 1) {
                binding.explanationCard.visibility = View.GONE
                binding.resultRow.visibility = View.GONE
                showQuestion(currentIndex + 1)
            }
        }
    }

    private fun onPrevious() {
        if (currentIndex > 0) {
            binding.explanationCard.visibility = View.GONE
            binding.resultRow.visibility = View.GONE
            showQuestion(currentIndex - 1)
        }
    }

    private fun finishQuiz() {
        // Make sure last question is recorded
        val score = correctAnswers.size
        val total = questions.size
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_SCORE, score)
            putExtra(ResultActivity.EXTRA_TOTAL, total)
            putExtra(ResultActivity.EXTRA_CHAPTER_NAME, chapterName)
        }
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
