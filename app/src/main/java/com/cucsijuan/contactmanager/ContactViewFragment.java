package com.cucsijuan.contactmanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static com.cucsijuan.contactmanager.DBHelper.COL_EMAIL;
import static com.cucsijuan.contactmanager.DBHelper.COL_LASTNAME;
import static com.cucsijuan.contactmanager.DBHelper.COL_NAME;
import static com.cucsijuan.contactmanager.DBHelper.COL_PHONE;
import static com.cucsijuan.contactmanager.DBHelper.COL_PHOTO;
import static com.cucsijuan.contactmanager.DBHelper.TABLE_CONTACTS;

public class ContactViewFragment extends Fragment {

    private TextView nameView;
    private TextView lastnameView;
    private TextView emailView;
    private TextView phoneView;
    private ImageView photoView;

    public ContactViewFragment() {
        // Required empty public constructor
    }

    protected void initializeContactView(){

        nameView = (TextView) getActivity().findViewById(R.id.view_name);
        lastnameView = (TextView) getActivity().findViewById(R.id.view_lastname);
        emailView = (TextView) getActivity().findViewById(R.id.view_email);
        phoneView = (TextView) getActivity().findViewById(R.id.view_phone_number);
        photoView = (ImageView) getActivity().findViewById(R.id.view_image_contact);

        Intent intent = getActivity().getIntent();
        final long id = intent.getLongExtra("personID", 0);
        final DBHelper bh = DBHelper.getInstance(getActivity());
        final String idString = Long.toString(id);

        SQLiteDatabase dbRead = bh.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " WHERE _id = ?", new String[]{ idString });
        cursor.moveToFirst();
        nameView.setText(cursor.getString(cursor.getColumnIndex(COL_NAME)));
        lastnameView.setText(cursor.getString(cursor.getColumnIndex(COL_LASTNAME)));
        emailView.setText(cursor.getString(cursor.getColumnIndex(COL_EMAIL)));
        phoneView.setText(cursor.getString(cursor.getColumnIndex(COL_PHONE)));
        String photo = cursor.getString(cursor.getColumnIndex(COL_PHOTO));
        if(photo != null)
        {
            photoView.setImageBitmap(BitmapFactory.decodeFile(photo));
        }
        else
        {
            photoView.setImageResource(android.R.drawable.sym_def_app_icon);
        }

        cursor.close();
        dbRead.close();

    }

    public String getPhone(){
        return phoneView.getText().toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_view, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.setFabEditVisible(true);
        MainActivity.getFab().setBackground(getActivity().getDrawable(R.drawable.ic_dialog_dial));
        initializeContactView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivity.setFabEditVisible(false);
    }
}
