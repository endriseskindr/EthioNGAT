package com.ngat.ethiopia.ui.chapters

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ngat.ethiopia.data.entity.Chapter
import com.ngat.ethiopia.databinding.ItemChapterBinding

class ChapterAdapter(
    private val onClick: (Chapter) -> Unit
) : ListAdapter<Chapter, ChapterAdapter.ChapterViewHolder>(ChapterDiffCallback()) {

    class ChapterViewHolder(
        private val binding: ItemChapterBinding,
        private val onClick: (Chapter) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chapter: Chapter) {
            binding.tvChapterNumber.text = chapter.id.toString()
            binding.tvChapterName.text = chapter.name
            binding.tvSection.text = chapter.section
            binding.tvItemCount.text = "${chapter.item_count} questions"
            binding.viewSectionColor.setBackgroundColor(Color.parseColor(chapter.getSectionColor()))
            binding.tvIcon.text = chapter.getSectionIcon()

            binding.root.setOnClickListener { onClick(chapter) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val binding = ItemChapterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ChapterViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ChapterDiffCallback : DiffUtil.ItemCallback<Chapter>() {
        override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter) = oldItem == newItem
    }
}
