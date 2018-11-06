package com.canberkozdemir.notecontact;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "notes")
public class Notes {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notes_id")
    private int notesId;
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;
    private String date;
    private String title;
    private String content;

    public Notes(String phoneNumber,String date,String title,String content){
        this.phoneNumber=phoneNumber;
        this.date=date;
        this.title=title;
        this.content=content;
    }

    public int getNotesId() {
        return notesId;
    }

    public void setNotesId(int notesId) {
        this.notesId = notesId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
