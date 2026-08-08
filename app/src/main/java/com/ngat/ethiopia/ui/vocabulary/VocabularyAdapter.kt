package com.ngat.ethiopia.ui.vocabulary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ngat.ethiopia.data.entity.Vocabulary
import com.ngat.ethiopia.databinding.ItemVocabularyBinding

class VocabularyAdapter(
    private val onClick: (Vocabulary) -> Unit
) : ListAdapter<Vocabulary, VocabularyAdapter.VocabViewHolder>(VocabDiffCallback()) {

    class VocabViewHolder(
        private val binding: ItemVocabularyBinding,
        private val onClick: (Vocabulary) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(v: Vocabulary) {
            binding.tvWord.text = v.word
            binding.tvPos.text = v.pos
            binding.tvDefinition.text = v.definition

            binding.root.setOnClickListener { onClick(v) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VocabViewHolder {
        val binding = ItemVocabularyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VocabViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: VocabViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class VocabDiffCallback : DiffUtil.ItemCallback<Vocabulary>() {
        override fun areItemsTheSame(old: Vocabulary, new: Vocabulary) = old.id == new.id
        override fun areContentsTheSame(old: Vocabulary, new: Vocabulary) = old == new
    }
}
