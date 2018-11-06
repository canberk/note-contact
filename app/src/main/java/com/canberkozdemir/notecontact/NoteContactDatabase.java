package com.canberkozdemir.notecontact;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Contacts.class,Notes.class},version = 1)
public abstract class NoteContactDatabase extends RoomDatabase {

    private static NoteContactDatabase noteContactDatabase;
    public abstract IContactDAO getContactDAO();
    public abstract INotesDAO getNotesDAO();

    private static final String DATABASE_NAME="NoteContactDb";

    public static NoteContactDatabase getDatabase(Context context){
        if(noteContactDatabase == null){
            noteContactDatabase =
                    Room.databaseBuilder(context.getApplicationContext(),
                            NoteContactDatabase.class,
                            DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return noteContactDatabase;
    }

    public static void destroyInstance(){
        noteContactDatabase = null;
    }

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}
