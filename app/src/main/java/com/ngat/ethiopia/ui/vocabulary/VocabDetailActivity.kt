package com.ngat.ethiopia.ui.vocabulary

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ngat.ethiopia.NigatApp
import com.ngat.ethiopia.data.entity.UserProgress
import com.ngat.ethiopia.data.entity.Vocabulary
import com.ngat.ethiopia.databinding.ActivityVocabDetailBinding
import com.ngat.ethiopia.util.SM2Algorithm
import kotlinx.coroutines.launch

class VocabDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVocabDetailBinding
    private val repository by lazy { (application as NigatApp).repository }

    private var vocab: Vocabulary? = null
    private var progress: UserProgress? = null
    private var definitionRevealed = false

    companion object {
        const val EXTRA_VOCAB_ID = "extra_vocab_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVocabDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Word Detail"

        val vocabId = intent.getStringExtra(EXTRA_VOCAB_ID) ?: return
        loadVocab(vocabId)

        binding.btnReveal.setOnClickListener { toggleReveal() }
        setupDifficultyButtons()
    }

    private fun loadVocab(id: String) {
        lifecycleScope.launch {
            vocab = repository.getVocab(id)
            progress = repository.getProgress("vocabulary", id)
            val cluster = vocab?.cluster_id?.let { repository.getCluster(it) }

            vocab?.let { v ->
                binding.tvWord.text = v.word
                binding.tvPos.text = v.getPosFull()
                binding.tvDefinition.text = v.definition
                binding.tvExample.text = "\"${v.example}\""
                binding.tvCluster.text = cluster?.name ?: "General"

                // SM-2 info
                progress?.let { p ->
                    binding.tvEaseFactor.text = "EF: %.2f".format(p.ease_factor)
                    binding.tvRepetitions.text = "Seen: ${p.repetitions}x"
                    binding.tvNextReview.text = "Next: ${SM2Algorithm.formatInterval(p.interval_days)}"
                    binding.sm2Card.visibility = View.VISIBLE
                } ?: run {
                    binding.sm2Card.visibility = View.GONE
                }

                // Start with definition hidden for flashcard mode
                definitionRevealed = false
                binding.definitionGroup.visibility = View.GONE
                binding.btnReveal.text = "Show Definition"
            }
        }
    }

    private fun toggleReveal() {
        definitionRevealed = !definitionRevealed
        if (definitionRevealed) {
            binding.definitionGroup.visibility = View.VISIBLE
            binding.btnReveal.visibility = View.GONE
            binding.difficultyGroup.visibility = View.VISIBLE
        }
    }

    private fun setupDifficultyButtons() {
        val buttons = listOf(
            binding.btnAgain to 0,
            binding.btnHard to 2,
            binding.btnGood to 4,
            binding.btnEasy to 5
        )
        buttons.forEach { (btn, quality) ->
            btn.setOnClickListener { recordReview(quality) }
        }
    }

    private fun recordReview(quality: Int) {
        vocab?.let { v ->
            lifecycleScope.launch {
                repository.recordReview("vocabulary", v.id, quality)
                progress = repository.getProgress("vocabulary", v.id)

                // Update SM-2 display
                progress?.let { p ->
                    binding.tvEaseFactor.text = "EF: %.2f".format(p.ease_factor)
                    binding.tvRepetitions.text = "Seen: ${p.repetitions}x"
                    binding.tvNextReview.text = "Next: ${SM2Algorithm.formatInterval(p.interval_days)}"
                    binding.sm2Card.visibility = View.VISIBLE
                }

                // Show confirmation
                binding.difficultyGroup.visibility = View.GONE
                binding.btnReveal.apply {
                    visibility = View.VISIBLE
                    text = "Reviewed! Tap for next"
                    setOnClickListener { finish() }
                }
            }
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
