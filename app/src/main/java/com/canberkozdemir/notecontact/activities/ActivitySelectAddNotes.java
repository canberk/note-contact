package com.canberkozdemir.notecontact.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.canberkozdemir.notecontact.customadapter.CustomAdapterSelectNotes;
import com.canberkozdemir.notecontact.NoteContactDatabase;
import com.canberkozdemir.notecontact.Notes;
import com.canberkozdemir.notecontact.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ActivitySelectAddNotes extends AppCompatActivity {
    private static final int REQUEST_CODE_USE_DATA = 1;
    Button addNotes;
    private Toolbar toolbar;
    MaterialSearchView searchView;
    ListView  selectNotes;
    List<Notes> notes;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_add_notes);

        initComponents();
        registerEventHandlers();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");

        startNotes();
    }

    private void initComponents(){
        addNotes=(Button) findViewById(R.id.add_notes);
        selectNotes=(ListView) findViewById(R.id.select_notes);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
    }

    private void registerEventHandlers(){
        addNotes_onClick();
        searchView_onSearchViewListener();
        searchView_onQueryTextListener();
    }

    private void startNotes(){
        NoteContactDatabase noteDb = NoteContactDatabase.getDatabase(getApplicationContext());
        notes = noteDb.getNotesDAO().getNotes(phoneNumber);
        CustomAdapterSelectNotes customAdapterSelectNotes= new CustomAdapterSelectNotes(ActivitySelectAddNotes.this,notes);
        selectNotes.setAdapter(customAdapterSelectNotes);
    }

    private void addNotes_onClick(){
        addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ActivityCreateOrChangeNotes.class);
                intent.putExtra("phoneNumber",phoneNumber);
                startActivityForResult(intent,REQUEST_CODE_USE_DATA);
            }
        });
    }

    private void searchView_onSearchViewListener(){
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                startNotes();
            }
        });
    }

    private void searchView_onQueryTextListener(){
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CustomAdapterSelectNotes customAdapterSelectNotes;
                if(newText != null && !newText.isEmpty()){
                    List<Notes> notesLastFound = new ArrayList<>();
                    Iterator iterator = notes.iterator();
                    while (iterator.hasNext()){
                        Notes nextNote = (Notes) iterator.next();
                        if(nextNote.getTitle().toLowerCase().contains(newText.toLowerCase()))
                            notesLastFound.add(nextNote);
                    }
                    customAdapterSelectNotes = new CustomAdapterSelectNotes(ActivitySelectAddNotes.this,notesLastFound);
                    selectNotes.setAdapter(customAdapterSelectNotes);
                }else{
                    customAdapterSelectNotes= new CustomAdapterSelectNotes(ActivitySelectAddNotes.this,notes);
                }
                selectNotes.setAdapter(customAdapterSelectNotes);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_USE_DATA) {
            if (resultCode == Activity.RESULT_OK) {
                String receiveText = data.getStringExtra("actionReturn");
                Toast.makeText(this, receiveText, Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, R.string.nothing_changed, Toast.LENGTH_SHORT).show();
            }
            NoteContactDatabase noteDb = NoteContactDatabase.getDatabase(getApplicationContext());
            notes = noteDb.getNotesDAO().getNotes(phoneNumber);
            CustomAdapterSelectNotes customAdapterSelectNotes= new CustomAdapterSelectNotes(ActivitySelectAddNotes.this,notes);
            selectNotes.setAdapter(customAdapterSelectNotes);
        }
    }

}
