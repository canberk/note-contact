package com.canberkozdemir.notecontact;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface IContactDAO {

    @Insert
    void insertContact(Contacts... contacts);

    @Update
    void updateContact(Contacts... contacts);

    @Delete
    void deleteContact(Contacts... contacts);

    @Query("SELECT * FROM contacts")
    List<Contacts> getAllContacts();

    @Query("SELECT * FROM contacts WHERE phone_number=:phoneNumber")
    Contacts getContact(String phoneNumber);

    @Query("DELETE  FROM contacts")
    void deleteAllContacts();
}
