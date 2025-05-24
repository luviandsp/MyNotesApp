package com.pbp.mynotesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pbp.mynotesapp.databinding.ItemNotesBinding
import com.pbp.mynotesapp.datamodel.Note

class NotesAdapter(
    private val notes: List<Note>,
    private val onItemClick: (Note) -> Unit,
    private val onEditClick: (Note) -> Unit,
    private val onDeleteClick: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    inner class NotesViewHolder(private var binding: ItemNotesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            with(binding) {
                tvTitle.text = note.title
                tvContent.text = note.content

                btnEdit.setOnClickListener { onEditClick(note) }
                btnDelete.setOnClickListener { onDeleteClick(note) }
                itemView.setOnClickListener { onItemClick(note) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val binding = ItemNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size
}