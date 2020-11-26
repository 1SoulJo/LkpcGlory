package com.lkpc.android.app.glory.ui.note

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.data.NoteDatabase
import com.lkpc.android.app.glory.entity.Note
import kotlinx.android.synthetic.main.activity_note_detail.*
import kotlinx.android.synthetic.main.tool_bar.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NoteDetailActivity : AppCompatActivity() {
    private var noteId : Int = -1
    private lateinit var note : Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        noteId = intent.getIntExtra("noteId", -1)
        val db = NoteDatabase.getDatabase(context = this)
        db.noteDao().loadById(noteId).observe(
            this, { note ->
                if (note == null) {
                    return@observe
                }

                this.note = note

                // title
                toolbar_title.text = note.title

                // main content
                note_detail_title.text = note.title
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CANADA)
                val lastModified = dateFormat.format(note.lastModified)
                note_detail_last_modified.text = lastModified
                note_detail_content.text = note.content
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

            R.id.note_menu_edit -> {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CANADA)
                val lastModified = dateFormat.format(note.lastModified)

                val i = Intent(this, NoteEditActivity::class.java)
                i.putExtra("id", noteId)
                i.putExtra("type", note.type)
                i.putExtra("title", note.title)
                i.putExtra("content", note.content)
                i.putExtra("contentId", note.contentId)
                i.putExtra("lastModified", lastModified)

                startActivity(i)
            }

            R.id.note_menu_share -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "${note.title} \n ${note.content}")
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }

            R.id.note_menu_delete -> {
                GlobalScope.launch {
                    val db = NoteDatabase.getDatabase(context = this@NoteDetailActivity)
                    db.noteDao().deleteAll(listOf(note.id))
                    finish()
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
}