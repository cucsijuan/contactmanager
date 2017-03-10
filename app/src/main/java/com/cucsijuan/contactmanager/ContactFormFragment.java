package com.cucsijuan.contactmanager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.cucsijuan.contactmanager.DBHelper.COL_EMAIL;
import static com.cucsijuan.contactmanager.DBHelper.COL_LASTNAME;
import static com.cucsijuan.contactmanager.DBHelper.COL_NAME;
import static com.cucsijuan.contactmanager.DBHelper.COL_PHONE;
import static com.cucsijuan.contactmanager.DBHelper.TABLE_CONTACTS;

public class ContactFormFragment extends Fragment
{
    private static final int REQUEST_CAMERA = 1888;
    private boolean fotoOk = false;
    private TextView nameView;
    private TextView lastnameView;
    private TextView emailView;
    private TextView phoneView;
    //private ImageView photoView;
    //private GoogleApiClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_contactform, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        nameView = (TextView) view.findViewById(R.id.nombre_de_persona);
        lastnameView = (TextView) view.findViewById(R.id.lastName);
        emailView = (TextView) view.findViewById(R.id.emailAddress);
        phoneView = (TextView) getView().findViewById(R.id.phoneNumber);
        //photoView = (ImageView) findViewById(R.id.image_contact);

//        Typeface font = Typeface.createFromAsset( getActivity().getAssets(), "fontawesome-webfont.ttf" );
//
//        TextView iconMailView = (TextView)view.findViewById(R.id.icon_mail);
//        iconMailView.setTypeface(font);
//
//        TextView iconPhoneView = (TextView)getView().findViewById(R.id.icon_phone);
//        iconPhoneView.setTypeface(font);
//
//        TextView editUserImage = (TextView)getView().findViewById(R.id.icon_edit);
//        editUserImage.setTypeface(font);
//
        TextView editImage = (TextView)getView().findViewById(R.id.edit_image);

        editImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
             Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

             File photoFile = null;
             try {
                 photoFile = createImageFile();
             } catch (IOException ex) {
                 // Error occurred while creating the File
                 Toast.makeText(getActivity(), "Error taking photo", Toast.LENGTH_LONG).show();
                 return;
             }
             if (photoFile != null) {
                 Uri photoURI = FileProvider.getUriForFile(getActivity(),
                         "com.ana_pc.contactlist",
                         photoFile);
                 intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                 startActivityForResult(intent, REQUEST_CAMERA);
             }
            }
            });

        Intent intent = getActivity().getIntent();
        final long id = intent.getLongExtra("personID", 0);
        final DBHelper bh = new DBHelper(getActivity());
        final String idString = Long.toString(id);
        if(id != 0)
        {
            SQLiteDatabase dbRead = bh.getReadableDatabase();
            Cursor cursor = dbRead.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " WHERE _id = ?", new String[]{ idString });
            cursor.moveToFirst();
            nameView.setText(cursor.getString(cursor.getColumnIndex("name")));
            lastnameView.setText(cursor.getString(cursor.getColumnIndex("lastname")));
            emailView.setText(cursor.getString(cursor.getColumnIndex("email")));
            phoneView.setText(cursor.getString(cursor.getColumnIndex("phone")));
            /*String photo = cursor.getString(cursor.getColumnIndex(COL_PHOTO));
            if(photo != null)
            {
                photoView.setImageBitmap(BitmapFactory.decodeFile(photo));
            }
            else
            {
                photoView.setImageResource(R.drawable.user_image);
            }*/

            cursor.close();
            dbRead.close();
        }

        getActivity().findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView nameEdit = (TextView)getActivity().findViewById(R.id.nombre_de_persona);
                TextView lastNameEdit = (TextView)getActivity().findViewById(R.id.lastName);
                TextView emailEdit = (TextView)getActivity().findViewById(R.id.emailAddress);
                TextView phoneEdit = (TextView)getActivity().findViewById(R.id.phoneNumber);

                String name = nameEdit.getText().toString();

                if (name == null || name.isEmpty())
                {
                    Toast.makeText(getActivity(), "Name can not be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                String lastName = lastNameEdit.getText().toString();
                String email = emailEdit.getText().toString();
                String phone = phoneEdit.getText().toString();

                SQLiteDatabase dbWrite = bh.getWritableDatabase();

                ContentValues cv = new ContentValues();
                cv.put(COL_NAME, name);
                cv.put(COL_LASTNAME, lastName);
                cv.put(COL_EMAIL, email);
                cv.put(COL_PHONE, phone);
                /*if(fotoOk)
                    cv.put(COL_PHOTO, mCurrentPhotoPath);*/

                Intent action = new Intent();

                if(id != 0)
                {
                    // Hacemos un update
                    dbWrite.update(TABLE_CONTACTS, cv, "_id = ?", new String[]{Long.toString(id)});
                    action.putExtra("create", "false");
                }
                else
                {
                    // Hacemos un insert
                    dbWrite.insert(TABLE_CONTACTS, null, cv);
                    action.putExtra("create", "true");
                }
                String fullname = (lastName != null && !lastName.equals("")) ? name + " " + lastName : name;
                action.putExtra("fullname", fullname);

                dbWrite.close();

                Intent intent = getActivity().getIntent();
                intent.putExtra("personID", id);
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.remove(ContactFormFragment.this);
                transaction.commit();


                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                //getActivity().setResult(getActivity().RESULT_OK, action);
                //getActivity().finish();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CAMERA){
            if(resultCode == Activity.RESULT_OK)
            {
                Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                ImageView image = (ImageView) getView().findViewById(R.id.image_contact);
                image.setImageBitmap(imageBitmap);
                fotoOk = true;
                return;
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


}
