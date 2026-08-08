package com.ngat.ethiopia.ui.chapters

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ngat.ethiopia.NigatApp
import com.ngat.ethiopia.data.entity.Chapter
import com.ngat.ethiopia.databinding.ActivityChaptersBinding
import com.ngat.ethiopia.ui.quiz.QuizActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChaptersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChaptersBinding
    private val repository by lazy { (application as NigatApp).repository }
    private lateinit var adapter: ChapterAdapter
    private var currentSection: String? = null
    private var allChapters: List<Chapter> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChaptersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Chapters"

        setupRecyclerView()
        setupTabs()
        loadChapters()
    }

    private fun setupRecyclerView() {
        adapter = ChapterAdapter { chapter ->
            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra(QuizActivity.EXTRA_MODE, QuizActivity.MODE_CHAPTER)
                putExtra(QuizActivity.EXTRA_CHAPTER_ID, chapter.id)
                putExtra(QuizActivity.EXTRA_CHAPTER_NAME, chapter.name)
            }
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupTabs() {
        lifecycleScope.launch {
            val sections = repository.getDistinctSections()
            binding.tabLayout.removeAllTabs()
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText("All"))
            sections.forEach { section ->
                binding.tabLayout.addTab(binding.tabLayout.newTab().setText(section))
            }

            binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    currentSection = if (tab?.position == 0) null else tab?.text?.toString()
                    filterChapters()
                }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    private fun loadChapters() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.getAllChapters().collectLatest { chapters ->
                    allChapters = chapters
                    filterChapters()
                }
            }
        }
    }

    private fun filterChapters() {
        val filtered = if (currentSection == null) {
            allChapters
        } else {
            allChapters.filter { it.section == currentSection }
        }
        adapter.submitList(filtered)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
