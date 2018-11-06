package com.canberkozdemir.notecontact;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface INotesDAO {

    @Insert
    void insertNote(Notes... notes);

    @Update
    void updateNote(Notes... notes);

    @Delete
    void deleteNote(Notes... notes);

    @Query("Select * FROM notes")
    List<Notes> getAllNotes();

    @Query("SELECT * FROM notes WHERE phone_number=:phoneNumber")
    List<Notes> getNotes(String phoneNumber);

    @Query("SELECT * FROM notes WHERE notes_id=:noteId")
    Notes getNote(int noteId);

    @Query("DELETE FROM notes WHERE notes_id=:notesId")
    void deleteNote(int notesId);


}
