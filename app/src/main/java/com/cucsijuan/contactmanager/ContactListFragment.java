package com.cucsijuan.contactmanager;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import static com.cucsijuan.contactmanager.DBHelper.COL_LASTNAME;
import static com.cucsijuan.contactmanager.DBHelper.COL_NAME;
import static com.cucsijuan.contactmanager.DBHelper.TABLE_CONTACTS;

public class ContactListFragment extends Fragment {

    private ListView lista;
    private DBHelper dbHelper;
    private CustomAdapter customAdapter;
    private static int REQUEST_CONTACT = 85;

    protected void initializeDB()
    {

        dbHelper = new DBHelper(getActivity());
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
        getActivity().findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Intent intent = new Intent(getActivity(), ContactFormFragment.class);
                startActivityForResult(intent, 85);*/
                getActivity().getIntent().putExtra("personID", 0);
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                ContactFormFragment fragment2 = new ContactFormFragment();
                fragment2.setTargetFragment(ContactListFragment.this, REQUEST_CONTACT);
                transaction.add(R.id.content_main,fragment2);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        lista.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
//                Intent intent = getActivity().getIntent();
//                intent.putExtra("personID", id);
//                FragmentManager manager = getFragmentManager();
//                FragmentTransaction transaction = manager.beginTransaction();
//                ContactPreviewFragment fragment3 = new ContactPreviewFragment();
//                transaction.replace(R.id.container, fragment3);
//                transaction.addToBackStack(null);
//                transaction.commit();

                /*Intent intent = new Intent(getActivity(), ContactPreviewFragment.class);
                intent.putExtra("personID", id);
                // TODO - not handled!
                startActivityForResult(intent,86);*/
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contactlist, container, false);
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

            // Extract properties from cursor
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COL_LASTNAME));

            // Populate fields with extracted properties
            if (!lastName.equals("") || !lastName.equals(null) ){
                nameView.setText(name + " " + lastName);
            }else{
                nameView.setText(name);
            }

        }
    }


//    public class CustomAdapter extends CursorAdapter {
//
//        public CustomAdapter(Context context, Cursor cursor){
//            super(context, cursor, true);
//        }
//
//        @Override
//        public View newView(Context context, Cursor cursor, ViewGroup parent) {
//            LayoutInflater li = getActivity().getLayoutInflater();
//            View newView = li.inflate(R.layout.row_persona,parent, false);
//
//            String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
//            String lastname = cursor.getString(cursor.getColumnIndex(COL_LASTNAME));
//            //String photo = cursor.getString(cursor.getColumnIndex(COL_PHOTO));
//
//            TextView nameView = (TextView) newView.findViewById(R.id.nombre_de_persona);
//            //ImageView photoView = (ImageView) newView.findViewById(R.id.contact_photo);
//
//            String fullname = (lastname != null && !lastname.equals("")) ? name + " " + lastname : name;
//            nameView.setText(fullname);
//            /*if(photo != null) {
//                photoView.setImageBitmap(BitmapFactory.decodeFile(photo));
//            }*/
//
//
//            return newView;
//        }
//
//        @Override
//        public void bindView(View view, Context context, Cursor cursor) {
//            String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
//            //String photo = cursor.getString(cursor.getColumnIndex(COL_PHOTO));
//
//            TextView nameView = (TextView) view.findViewById(R.id.nombre_de_persona);
//            //ImageView photoView = (ImageView) view.findViewById(R.id.contact_photo);
//
//            nameView.setText(name);
//            /*if(photo != null)
//            {
//                photoView.setImageBitmap(BitmapFactory.decodeFile(photo));
//            }
//            else
//            {
//                photoView.setImageResource(R.drawable.user_image);
//            }*/
//        }
//    }



}
