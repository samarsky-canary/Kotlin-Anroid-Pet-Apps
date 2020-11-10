package com.example.nodeapp

import DbManager
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ticket.view.*

class MainActivity : AppCompatActivity() {
    val appID = "notesapp-vlzgv";
    var Notes = ArrayList<Note>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        //load from db
        LoadNotesFromDB("%");
    }

    override fun onResume() {
        super.onResume()
        LoadNotesFromDB("%");
    }



    fun LoadNotesFromDB(title: String) {
        val dbManager = DbManager(this);
        var projection = arrayOf("id", "title", "content");
        val selectionArgs = arrayOf(title);
        val cursor = dbManager.Query(projection, "title like ?", selectionArgs, "title");
        Notes.clear();
        if (cursor.moveToFirst()) {
            do {
                val (id, title, content) = extractNoteFromCursor(cursor, projection)
                Notes.add(Note(id, title, content));
            } while (cursor.moveToNext());
        }
        var NotesAdapter = NoteViewAdapter(Notes, this);
        lvNotes.adapter = NotesAdapter;
    }

    private fun extractNoteFromCursor(
        cursor: Cursor,
        projection: Array<String>
    ): Triple<Int, String, String> {
        val id = cursor.getInt(cursor.getColumnIndex(projection[0]));
        val title = cursor.getString(cursor.getColumnIndex(projection[1]));
        val content = cursor.getString(cursor.getColumnIndex(projection[2]));
        return Triple(id, title, content)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu);
        onSearchOpen(menu);
        return super.onCreateOptionsMenu(menu)
    }

    fun onSearchOpen(menu: Menu?) {
        var noteSearchView = menu!!.findItem(R.id.app_search).actionView as SearchView;
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager;
        noteSearchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        noteSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                LoadNotesFromDB("%" + query + "%");
                return false;
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false;
            }
        });
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miAddNoteMenu -> {
                var intent = Intent(this, NodesAddActivity::class.java);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item)
    }






    inner class NoteViewAdapter : BaseAdapter {
        var notes = ArrayList<Note>();
        var context: Context;
        constructor(notes: ArrayList<Note>, context: Context) : super() {
            this.notes = notes;
            this.context= context;
        }

        override fun getItem(p0: Int): Any {
            return notes[p0];
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong();
        }

        override fun getCount(): Int {
            return notes.size;
        }

        override fun getView(pos: Int, p1: View?, p2: ViewGroup?): View {
            var myView = layoutInflater.inflate(R.layout.ticket, null);
            var myNote = notes[pos];
            myView.tvNodeTitle.text = myNote.name;
            myView.tvNodeContent.text = myNote.nodeDes;
            myView.ivDeleteButton.setOnClickListener(View.OnClickListener {
                val dbManger = DbManager(this.context);
                var selectionArgs = arrayOf(myNote.nodeID.toString());
                val ifDeleted = dbManger.Delete("id=?", selectionArgs);
                LoadNotesFromDB("%");
            });

            myView.ivEditButton.setOnClickListener(View.OnClickListener {
                var intent = Intent(this.context,NodesAddActivity::class.java);
                intent.putExtra("id", myNote.nodeID);
                intent.putExtra("title", myNote.name);
                intent.putExtra("content", myNote.nodeDes);
                startActivity(intent);
            })
            return myView;
        }

    }
}