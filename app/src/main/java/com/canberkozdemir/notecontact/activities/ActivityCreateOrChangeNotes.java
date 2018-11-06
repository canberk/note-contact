package com.canberkozdemir.notecontact.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.canberkozdemir.notecontact.NoteContactDatabase;
import com.canberkozdemir.notecontact.Notes;
import com.canberkozdemir.notecontact.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityCreateOrChangeNotes extends AppCompatActivity {
    TextView date;
    EditText title,content;
    Button changeOrCreateNotes,deleteNotes;
    String action,titleText,dateText,contentText,currentDate,phoneNumber;
    int notesId;
    int id;
    SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_change_notes);

        initComponents();
        registerEventHandlers();

        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        id = intent.getIntExtra("id",0);
        notesId=intent.getIntExtra("notesId",0);
        phoneNumber = intent.getStringExtra("phoneNumber");
        titleText = intent.getStringExtra("title");
        dateText = intent.getStringExtra("date");
        contentText = intent.getStringExtra("content");
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        currentDate = sdf.format(new Date());

        if(action!=null){
            title.setText(titleText);
            date.setText(dateText);
            content.setText(contentText);
            changeOrCreateNotes.setText(R.string.button_edit_text);
        }else{
            date.setText(currentDate);
            changeOrCreateNotes.setText(R.string.button_add_text);
            deleteNotes.setVisibility(View.INVISIBLE);
        }
    }

    private void initComponents(){
        title= (EditText) findViewById(R.id.change_title);
        date = (TextView) findViewById(R.id.change_date);
        content = (EditText) findViewById(R.id.change_content);
        changeOrCreateNotes = (Button) findViewById(R.id.change_or_create_notes);
        deleteNotes = (Button) findViewById(R.id.delete_notes);
    }

    private void registerEventHandlers(){
        changeOrCreateNotes_onClick();
        deleteNotes_onClick();
    }

    private void changeOrCreateNotes_onClick(){
        changeOrCreateNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteContactDatabase noteDb = NoteContactDatabase.getDatabase(getApplicationContext());
                Intent returnIntent = new Intent();
                if(action!=null){
                    Notes note=noteDb.getNotesDAO().getNote(notesId);
                    note.setTitle(title.getText().toString());
                    note.setContent(content.getText().toString());
                    noteDb.getNotesDAO().updateNote(note);
                    returnIntent.putExtra("actionReturn",title.getText().toString()+" "+getResources().getString(R.string.edited_text));
                }else{
                    noteDb.getNotesDAO().insertNote(new Notes(phoneNumber,currentDate,title.getText().toString(),content.getText().toString()));
                    returnIntent.putExtra("actionReturn",title.getText().toString()+" "+getResources().getString(R.string.added_text));
                }
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private void deleteNotes_onClick(){
        deleteNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCreateOrChangeNotes.this);
                builder.setTitle(R.string.are_you_sure);
                builder.setMessage(R.string.are_you_sure_delete);
                builder.setIcon(R.drawable.ic_action_action_search);
                AlertDialogClickListener alertDialogClickListener = new AlertDialogClickListener();
                builder.setPositiveButton(R.string.yes, alertDialogClickListener);
                builder.setNegativeButton(R.string.no, alertDialogClickListener);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    class AlertDialogClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which == AlertDialog.BUTTON_NEGATIVE) {
                Toast.makeText(ActivityCreateOrChangeNotes.this,R.string.nothing_changed,Toast.LENGTH_SHORT).show();
            } else if (which == AlertDialog.BUTTON_POSITIVE){
                Intent returnIntent = new Intent();
                NoteContactDatabase noteDb = NoteContactDatabase.getDatabase(getApplicationContext());
                noteDb.getNotesDAO().deleteNote(id);
                returnIntent.putExtra("actionReturn",title.getText().toString()+" "+getResources().getString(R.string.deleted_text));
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        }
    }

}
