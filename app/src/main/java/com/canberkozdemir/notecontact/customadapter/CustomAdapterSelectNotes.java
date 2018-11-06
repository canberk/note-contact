package com.canberkozdemir.notecontact.customadapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canberkozdemir.notecontact.activities.ActivityCreateOrChangeNotes;
import com.canberkozdemir.notecontact.Notes;
import com.canberkozdemir.notecontact.R;

import java.util.List;

public class CustomAdapterSelectNotes  extends BaseAdapter {
    private static final int REQUEST_CODE_USE_DATA = 1;
    private Context context;
    private List<Notes> notes;

    public CustomAdapterSelectNotes(Context context,List<Notes> notes){
        this.context=context;
        this.notes= notes;
    }

    @Override
    public int getCount() {
        return  notes.size();
    }

    @Override
    public Object getItem(int position) {
        return  notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Notes notes = (Notes) getItem(position);
        final LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.select_custom,null);

        final TextView title=(TextView) layout.findViewById(R.id.select_title);
        TextView date=(TextView) layout.findViewById(R.id.select_date);
        final TextView content=(TextView) layout.findViewById(R.id.select_content);
        Button changeNotes=(Button) layout.findViewById(R.id.select_change_notes);

        title.setText(notes.getTitle());
        date.setText(notes.getDate().toString());
        content.setText(notes.getContent());
        changeNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ActivityCreateOrChangeNotes.class);
                intent.putExtra("title",notes.getTitle());
                intent.putExtra("date",notes.getDate());
                intent.putExtra("content",notes.getContent());
                intent.putExtra("action","change");
                intent.putExtra("id",notes.getNotesId());
                intent.putExtra("phoneNumber",notes.getPhoneNumber());
                intent.putExtra("notesId",notes.getNotesId());
                ((Activity) context).startActivityForResult(intent,REQUEST_CODE_USE_DATA);
            }
        });
        return layout;
    }
}
