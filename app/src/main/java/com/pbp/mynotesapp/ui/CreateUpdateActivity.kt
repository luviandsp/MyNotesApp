package com.pbp.mynotesapp.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pbp.mynotesapp.R
import com.pbp.mynotesapp.databinding.ActivityCreateUpdateBinding
import com.pbp.mynotesapp.datamodel.Note
import com.pbp.mynotesapp.db.DBHelper

class CreateUpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateUpdateBinding
    private lateinit var dbHelper: DBHelper
    private var note: Note? = null

    companion object {
        private const val TAG = "CreateUpdateActivity"
        const val EXTRA_NOTE = "extra_note"
        const val INTENT_SOURCE = "intent_source"
        const val CREATE_NOTE = "create"
        const val EDIT_NOTE = "edit"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateUpdateBinding.inflate(layoutInflater)
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

            toolbar.setNavigationOnClickListener {
                finish()
            }

            if (intent.getStringExtra(INTENT_SOURCE) == EDIT_NOTE) {
                toolbar.title = getString(R.string.edit_note)

                note = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_NOTE, Note::class.java)
                } else {
                    intent.getParcelableExtra(EXTRA_NOTE)
                }

                tietTitle.setText(note?.title)
                tietContent.setText(note?.content)
            } else {
                toolbar.title = getString(R.string.create_note)
            }

            btnSave.setOnClickListener {
                var title = tietTitle.text.toString().trim()
                var content = tietContent.text.toString().trim()

                if (title.isEmpty()) {
                    title = getString(R.string.untitled)
                }

                if (intent.getStringExtra(INTENT_SOURCE) == EDIT_NOTE) {
                    dbHelper.updateNote(note?.id ?: 0, title, content)
                    Log.d(TAG, "initViews: $note")
                    Toast.makeText(this@CreateUpdateActivity, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                } else {
                    dbHelper.insertNote(title, content)
                    Toast.makeText(this@CreateUpdateActivity, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                }

                finish()
            }
        }
    }
}