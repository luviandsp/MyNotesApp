package com.pbp.mynotesapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.pbp.mynotesapp.R
import com.pbp.mynotesapp.adapter.NotesAdapter
import com.pbp.mynotesapp.databinding.ActivityMainBinding
import com.pbp.mynotesapp.datamodel.Note
import com.pbp.mynotesapp.db.DBHelper
import kotlin.collections.listOf

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelper

    private lateinit var notesAdapter: NotesAdapter
    private var notes = mutableListOf<Note>()

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelper(this)

        initViews()
    }

    private fun initViews() {
        with(binding) {
            notesAdapter = NotesAdapter(
                notes = notes,
                onItemClick = { note ->
                    readNotes(note)
                },
                onEditClick = { note ->
                    editNote(note)
                },
                onDeleteClick = { note ->
                    deleteNote(note)
                }
            )

            rvNotes.apply {
                adapter = notesAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }

            fabCreateNote.setOnClickListener {
                createNote()
            }
        }
    }

    private fun deleteNote(note: Note) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_confirmation))
            .setMessage(getString(R.string.delete_confirmation_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                dbHelper.deleteNote(note.id)
                notes.remove(note)
                notesAdapter.notifyDataSetChanged()

                if (notes.isEmpty()) {
                    binding.rvNotes.visibility = View.GONE
                    binding.ivPlaceholder.visibility = View.VISIBLE
                }

                Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun editNote(note: Note) {
        val intent = Intent(this@MainActivity, CreateUpdateActivity::class.java)
        intent.apply {
            putExtra(CreateUpdateActivity.EXTRA_NOTE, note)
            putExtra(CreateUpdateActivity.INTENT_SOURCE, CreateUpdateActivity.EDIT_NOTE)
        }
        startActivity(intent)
    }

    private fun createNote() {
        val intent = Intent(this@MainActivity, CreateUpdateActivity::class.java)
        intent.putExtra(CreateUpdateActivity.INTENT_SOURCE, CreateUpdateActivity.CREATE_NOTE)
        startActivity(intent)
    }

    private fun readNotes(note: Note) {
        val intent = Intent(this@MainActivity, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_NOTE, note)
        startActivity(intent)
    }

    private fun loadNotes() {
        notes.clear()
        notes.addAll(dbHelper.getAllNotes())
        notesAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()

        loadNotes()

        if (notes.isEmpty()) {
            binding.rvNotes.visibility = View.GONE
            binding.ivPlaceholder.visibility = View.VISIBLE
        } else {
            binding.rvNotes.visibility = View.VISIBLE
            binding.ivPlaceholder.visibility = View.GONE
        }
    }
}