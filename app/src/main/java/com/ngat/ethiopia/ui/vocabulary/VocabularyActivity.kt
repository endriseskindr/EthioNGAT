package com.ngat.ethiopia.ui.vocabulary

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ngat.ethiopia.NigatApp
import com.ngat.ethiopia.data.entity.Cluster
import com.ngat.ethiopia.data.entity.Vocabulary
import com.ngat.ethiopia.databinding.ActivityVocabularyBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class VocabularyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVocabularyBinding
    private val repository by lazy { (application as NigatApp).repository }
    private lateinit var adapter: VocabularyAdapter

    private val searchQuery = MutableStateFlow("")
    private var selectedClusterId: Int? = null
    private var allVocab: List<Vocabulary> = emptyList()
    private var clusters: List<Cluster> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVocabularyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Vocabulary"

        setupRecyclerView()
        setupSearch()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = VocabularyAdapter { vocab ->
            val intent = Intent(this, VocabDetailActivity::class.java).apply {
                putExtra(VocabDetailActivity.EXTRA_VOCAB_ID, vocab.id)
            }
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true
            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery.value = newText.orEmpty()
                return true
            }
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchQuery
                    .debounce(200)
                    .distinctUntilChanged()
                    .collectLatest { query ->
                        filterAndDisplay(query)
                    }
            }
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.getAllVocabulary().collectLatest { vocab ->
                    allVocab = vocab
                    filterAndDisplay(searchQuery.value)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.getAllClusters().collectLatest { cls ->
                    clusters = cls
                    setupClusterChips()
                }
            }
        }
    }

    private fun setupClusterChips() {
        binding.chipGroup.removeAllViews()

        // "All" chip
        val allChip = Chip(this).apply {
            text = "All (${allVocab.size})"
            isCheckable = true
            isChecked = selectedClusterId == null
            setOnClickListener {
                selectedClusterId = null
                updateChipSelection()
                filterAndDisplay(searchQuery.value)
            }
        }
        binding.chipGroup.addView(allChip)

        // Top clusters (first 30 most populated)
        clusters.sortedByDescending { it.item_count }.take(30).forEach { cluster ->
            val chip = Chip(this).apply {
                text = "${cluster.name} (${cluster.item_count})"
                isCheckable = true
                isChecked = selectedClusterId == cluster.id
                tag = cluster.id
                setOnClickListener {
                    selectedClusterId = cluster.id
                    updateChipSelection()
                    filterAndDisplay(searchQuery.value)
                }
            }
            binding.chipGroup.addView(chip)
        }
    }

    private fun updateChipSelection() {
        for (i in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(i) as Chip
            val chipClusterId = chip.tag as? Int
            chip.isChecked = if (selectedClusterId == null) i == 0 else chipClusterId == selectedClusterId
        }
    }

    private fun filterAndDisplay(query: String) {
        var filtered = allVocab

        if (selectedClusterId != null) {
            filtered = filtered.filter { it.cluster_id == selectedClusterId }
        }

        if (query.isNotBlank()) {
            val q = query.lowercase()
            filtered = filtered.filter {
                it.word.lowercase().contains(q) ||
                it.definition.lowercase().contains(q)
            }
        }

        adapter.submitList(filtered)
        binding.emptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.tvResultCount.text = "${filtered.size} word${if (filtered.size != 1) "s" else ""}"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
