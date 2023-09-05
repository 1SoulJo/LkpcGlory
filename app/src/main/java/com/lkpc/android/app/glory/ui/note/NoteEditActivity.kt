package com.lkpc.android.app.glory.ui.note

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.data.NoteDatabase
import com.lkpc.android.app.glory.databinding.ActivityNoteEditBinding
import com.lkpc.android.app.glory.entity.Note
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class NoteEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.action_bar)

        // title
        findViewById<TextView>(R.id.ab_title).text = getString(R.string.my_notes)

        // content
        binding.noteTitle.setText(intent.getStringExtra("title"))
        binding.noteContent.setText(intent.getStringExtra("content"))

        // back button
        val backBtn: ImageView = findViewById(R.id.ab_btn_back)
        backBtn.visibility = View.VISIBLE
        backBtn.setOnClickListener {
            setResult(-1)
            finish()
        }

        // save button
        val saveBtn: TextView = findViewById(R.id.ab_btn_save)
        val id = intent.getIntExtra("id", -1)
        saveBtn.visibility = View.VISIBLE
        saveBtn.setOnClickListener {
            GlobalScope.launch {
                val note = Note(
                    type = intent.getStringExtra("type"),
                    contentId = intent.getStringExtra("contentId"),
                    title = binding.noteTitle.text.toString(),
                    content = binding.noteContent.text.toString(),
                    lastModified = Date().time
                )
                if (id > -1) {
                    note.id = id
                }

                val db = NoteDatabase.getDatabase(context = applicationContext)
                val saved = db.noteDao().insert(note)
                setResult(saved.toInt())
                finish()
            }
        }
    }
}