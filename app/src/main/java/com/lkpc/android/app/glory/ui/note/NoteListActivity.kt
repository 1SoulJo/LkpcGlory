package com.lkpc.android.app.glory.ui.note

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.databinding.ActivityNoteListBinding

class NoteListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        findViewById<TextView>(R.id.toolbar_title).setText(R.string.my_notes)

        val viewModel: NoteViewModel by viewModels()
        binding.rvNoteList.layoutManager = LinearLayoutManager(this)
        binding.rvNoteList.adapter = viewModel.adapter
        viewModel.getAllNotes().observe(this) { notes ->
            if (notes.isEmpty()) {
                binding.emptyTextArea.visibility = View.VISIBLE
            }
            viewModel.adapter.swapData(notes)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu!!.findItem(R.id.note_menu_share).isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

            R.id.note_menu_delete -> {
                val viewModel: NoteViewModel by viewModels()
                findViewById<Toolbar>(R.id.toolbar).startActionMode(viewModel.callback)!!
            }
        }

        return super.onOptionsItemSelected(item)
    }
}