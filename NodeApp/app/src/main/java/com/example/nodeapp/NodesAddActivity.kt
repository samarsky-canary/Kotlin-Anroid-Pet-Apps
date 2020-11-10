package com.example.nodeapp

import DbManager
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_nodes_add.*
import java.lang.Exception

class NodesAddActivity : AppCompatActivity() {
    var id = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nodes_add)

        id = 0;
        var bundle: Bundle? = intent.extras ?: return;
        id = bundle!!.getInt("id", 0)
        if (id != 0) {
            etTitleForAdd.setText(bundle.getString("title"))
            etContentAdd.setText(bundle.getString("content"));
        }
    }

    fun btAdd(view: View) {
        var dbManager = DbManager(this);
        val newNote = ContentValues();
        newNote.put("title", etTitleForAdd.text.toString());
        newNote.put("content", etContentAdd.text.toString());

        //Add new
        if (id == 0) {
            val id = dbManager.Insert(newNote);
            if (id > 0) {
                Toast.makeText(this, "New note added", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Note not added", Toast.LENGTH_LONG).show();
            }
            //Update
        } else {
            var selectionArgs = arrayOf(id.toString());
            val id = dbManager.Update(newNote, "id=?", selectionArgs);
            Toast.makeText(this, "Edited", Toast.LENGTH_LONG).show();
         }
    }
}