package com.canberkozdemir.notecontact.customadapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canberkozdemir.notecontact.Contacts;
import com.canberkozdemir.notecontact.R;

import java.util.List;

public  class CustomAdapterContactList extends BaseAdapter {
    private Context context;
    private List<Contacts> contacts;


    public CustomAdapterContactList(Context context,List<Contacts> contacts){
        this.context=context;
        this.contacts=contacts;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Contacts contacts = (Contacts) getItem(position);
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        LinearLayout layout = null;

        if(convertView == null){
            layout = (LinearLayout) layoutInflater.inflate(R.layout.contacts_custom,null);
        }else {
            layout = (LinearLayout) convertView;
        }

        ImageView imageView = (ImageView) layout.findViewById(R.id.photo);
        TextView textViewName = (TextView) layout.findViewById(R.id.name);
        TextView textViewPhone = (TextView) layout.findViewById(R.id.phone_number);

        Bitmap bitmap = BitmapFactory.decodeByteArray(contacts.getPhoto(), 0, contacts.getPhoto().length);

        imageView.setImageBitmap(bitmap);
        textViewName.setText(contacts.getName());
        textViewPhone.setText(contacts.getPhoneNumber());
        
        return layout;
    }
}
