package com.canberkozdemir.notecontact.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.canberkozdemir.notecontact.Contacts;
import com.canberkozdemir.notecontact.customadapter.CustomAdapterContactList;
import com.canberkozdemir.notecontact.NoteContactDatabase;
import com.canberkozdemir.notecontact.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class ActivityContacts extends AppCompatActivity {

    private static final int REQUEST_READ_CONTACTS = 444;
    private ListView cList;
    private FloatingActionButton floatingActionButton;
    private ProgressDialog pDialog;
    private Toolbar toolbar;
    private MaterialSearchView searchView;
    private Handler updateBarHandler;
    private List<Contacts> contact;
    Cursor cursor;
    int counter;
    boolean isFirstTime,isUITask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        initComponents();
        registerEventHandlers();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));

        updateBarHandler = new Handler();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        isFirstTime = preferences.getBoolean("isFirstTime", true);
        if(isFirstTime){
           startContact();
        }else{
            nTimeStart();
            refreshContactOnBackground();
        }
    }

    private void initComponents(){
        cList = (ListView) findViewById(R.id.contacts_list);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.contact_float_button);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
    }

    private void registerEventHandlers(){
        contactList_onClick();
        floatingActionButton_onClick();
        searchView_onSearchViewListener();
        searchView_onQueryTextListener();
    }

    private void contactList_onClick(){
        cList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Contacts contacts = (Contacts) cList.getItemAtPosition(position);
                Intent intent=new Intent(getApplicationContext(), ActivitySelectAddNotes.class);
                intent.putExtra("phoneNumber",contacts.getPhoneNumber());
                startActivity(intent);
            }
        });
    }

    private void floatingActionButton_onClick (){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshContactOnBackground();
            }
        });
    }

    private void refreshContactOnBackground(){
        floatingActionButton.setVisibility(View.INVISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                isUITask=false;
                getContacts();
            }
        }).start();
    }

    private void searchView_onSearchViewListener(){
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                floatingActionButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSearchViewClosed() {
                nTimeStart();
                floatingActionButton.setVisibility(View.VISIBLE);
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
                CustomAdapterContactList customAdapterContactList;
                if(newText != null && !newText.isEmpty()){
                    List<Contacts> contactLastFound=new ArrayList<>();
                    Iterator iterator = contact.iterator();
                    while (iterator.hasNext()){
                        Contacts nextContact = (Contacts) iterator.next();
                        if(nextContact.getName().toLowerCase().contains(newText.toLowerCase()))
                            contactLastFound.add(nextContact);
                    }
                    customAdapterContactList = new CustomAdapterContactList(ActivityContacts.this,contactLastFound);
                    cList.setAdapter(customAdapterContactList);
                }else{
                    customAdapterContactList = new CustomAdapterContactList(ActivityContacts.this,contact);
                }
                cList.setAdapter(customAdapterContactList);
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
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    private void startContact(){
        pDialog = new ProgressDialog(ActivityContacts.this);
        pDialog.setMessage(getResources().getString(R.string.reading_contact)+"...");
        pDialog.setCancelable(false);
        pDialog.show();
        isUITask=true;
        if(!mayRequestContacts()){
            pDialog.cancel();
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getContacts();
                }
            }).start();
        }
    }

    private void nTimeStart(){
        NoteContactDatabase noteDb = NoteContactDatabase.getDatabase(getApplicationContext());
        contact = noteDb.getContactDAO().getAllContacts();
        CustomAdapterContactList customAdapterContactList = new CustomAdapterContactList(ActivityContacts.this,contact);
        cList.setAdapter(customAdapterContactList);
    }

    public void getContacts() {
        String phoneNumber = null;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
        contact = new ArrayList<>();

        if (cursor.getCount() > 0) {
            counter = 0;
            while (cursor.moveToNext()) {
                if(isUITask) {
                    updateBarHandler.post(new Runnable() {
                        public void run() {
                            pDialog.setMessage(getResources().getString(R.string.reading_contact) + " : " + counter++ + "/" + cursor.getCount());
                        }
                    });
                }
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?",
                            new String[]{contact_id}, null);
                    phoneCursor.moveToNext();
                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                    Bitmap photo = null;
                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contact_id)));
                    if (inputStream != null) {
                         photo = BitmapFactory.decodeStream(inputStream);
                    }else
                        photo = BitmapFactory.decodeResource(this.getResources(),R.drawable.pp);
                    contact.add(new Contacts(name,phoneNumber,getBytes(photo)));
                    phoneCursor.close();
                }
            }
            NoteContactDatabase noteDb = NoteContactDatabase.getDatabase(getApplicationContext());
            for(int i=0;i<contact.size();i++){
                noteDb.getContactDAO().insertContact(new Contacts(
                        contact.get(i).getName(),
                        contact.get(i).getPhoneNumber(),
                        contact.get(i).getPhoto()
                ));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    nTimeStart();
                    if(isUITask){
                    pDialog.cancel();
                    }else{
                        Toast.makeText(getApplicationContext(),R.string.update_completed,Toast.LENGTH_SHORT).show();
                    }
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
            });
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstTime", false);
            editor.commit();
        }
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startContact();
            }
        }
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
}
