package com.canberkozdemir.notecontact;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "contacts")
public class Contacts {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int contactId;
    @ColumnInfo(name = "name")
    private String name;
    @NonNull
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] photo;

    public Contacts(String name, String phoneNumber, byte[] photo) {
        this.name=name;
        this.phoneNumber=phoneNumber;
        this.photo=photo;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
