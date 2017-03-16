package com.cucsijuan.contactmanager;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import static com.cucsijuan.contactmanager.DBHelper.COL_LASTNAME;
import static com.cucsijuan.contactmanager.DBHelper.COL_NAME;
import static com.cucsijuan.contactmanager.DBHelper.COL_PHONE;
import static com.cucsijuan.contactmanager.DBHelper.COL_PHOTO;
import static com.cucsijuan.contactmanager.DBHelper.TABLE_CONTACTS;

public class ContactListFragment extends Fragment {

    private ListView lista;
    private DBHelper dbHelper;
    private CustomAdapter customAdapter;
    private static int REQUEST_CONTACT = 85;

    protected void initializeDB()
    {

        dbHelper = DBHelper.getInstance(getActivity());
        // recupero datos de la base de datos
        SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
        Cursor cursor = dbWrite.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " ORDER BY name ASC", null);

        customAdapter = new CustomAdapter(getActivity(), cursor);
        lista = (ListView)getActivity().findViewById(R.id.lista);
        lista.setAdapter(customAdapter);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                initializeDB();
                return;
            }
        }
    }

    public ContactListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        this.initializeDB();

        lista.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Intent intent = getActivity().getIntent();
                intent.putExtra("personID", id);

                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                ContactViewFragment contactViewFragment = new ContactViewFragment();
                MainActivity.setContactViewFragment(contactViewFragment);
                MainActivity.getFab().setImageDrawable(getActivity().getDrawable(R.drawable.ic_dialog_dial));
                transaction.replace(R.id.content_main, contactViewFragment,"editFragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contactlist, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();


    }



    public class CustomAdapter extends CursorAdapter {
        public CustomAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        // The newView method is used to inflate a new view and return it,
        // you don't bind any data to the view at this point.
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.row_persona, parent, false);
        }

        // The bindView method is used to bind all data to a given view
        // such as setting the text on a TextView.
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Find fields to populate in inflated template
            TextView nameView = (TextView) view.findViewById(R.id.nombre_de_persona);
            TextView phoneView = (TextView) view.findViewById(R.id.row_phone);
            ImageView image = (ImageView) view.findViewById(R.id.contact_photo);

            // Extract properties from cursor
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COL_LASTNAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE));

            phoneView.setText(phone);

            if (cursor.getString(cursor.getColumnIndexOrThrow(COL_PHOTO))!=null){
                Bitmap imageBitmap = BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndexOrThrow(COL_PHOTO)));
                image.setImageBitmap(imageBitmap);
            }

            // Populate fields with extracted properties
            if (!lastName.equals("") || !lastName.equals(null) ){
                nameView.setText(name + " " + lastName);
            }else{
                nameView.setText(name);
            }



        }
    }




}
