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
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.cucsijuan.contactmanager.DBHelper.COL_EMAIL;
import static com.cucsijuan.contactmanager.DBHelper.COL_LASTNAME;
import static com.cucsijuan.contactmanager.DBHelper.COL_NAME;
import static com.cucsijuan.contactmanager.DBHelper.COL_PHONE;
import static com.cucsijuan.contactmanager.DBHelper.TABLE_CONTACTS;
import static com.cucsijuan.contactmanager.DBHelper.COL_PHOTO;
import static com.google.android.gms.wearable.DataMap.TAG;

public class ContactFormFragment extends Fragment {
    private static final int REQUEST_CAMERA = 1888;
    private boolean fotoOk = false;
    private TextView nameView;
    private TextView lastnameView;
    private TextView emailView;
    private TextView phoneView;
    private ImageView photoView;
    private String mCurrentPhotoPath;
    private long id;
    private DBHelper bh;
    //private GoogleApiClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contactform, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameView = (TextView) view.findViewById(R.id.edit_name);
        lastnameView = (TextView) view.findViewById(R.id.edit_last_name);
        emailView = (TextView) view.findViewById(R.id.edit_email_address);
        phoneView = (TextView) getView().findViewById(R.id.edit_phone_number);
        photoView = (ImageView) getView().findViewById(R.id.edit_image_contact);

        FragmentManager manager = getChildFragmentManager();
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                manager.findFragmentById(R.id.place_autocomplete_fragment);

        TextView editImage = (TextView) getView().findViewById(R.id.edit_image);

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            "com.cucsijuan.contactmanager",photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
            }
        });

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }

        });

        Intent intent = getActivity().getIntent();
        id = intent.getLongExtra("personID", 0);
        bh = DBHelper.getInstance(getActivity());;
        final String idString = Long.toString(id);

        if (id != 0) {
            SQLiteDatabase dbRead = bh.getReadableDatabase();
            Cursor cursor = dbRead.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " WHERE _id = ?", new String[]{idString});
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
    }

    public boolean saveContact(){
        TextView nameEdit = (TextView) getActivity().findViewById(R.id.edit_name);
        TextView lastNameEdit = (TextView) getActivity().findViewById(R.id.edit_last_name);
        TextView emailEdit = (TextView) getActivity().findViewById(R.id.edit_email_address);
        TextView phoneEdit = (TextView) getActivity().findViewById(R.id.edit_phone_number);

        String name = nameEdit.getText().toString();

        if (name == null || name.isEmpty()) {
            Toast.makeText(getActivity(), "Name can not be empty", Toast.LENGTH_LONG).show();
            return false;
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
        if(fotoOk)
            cv.put(COL_PHOTO, mCurrentPhotoPath);

        Intent action = new Intent();

        if (id != 0) {
            // Hacemos un update
            dbWrite.update(TABLE_CONTACTS, cv, "_id = ?", new String[]{Long.toString(id)});
            action.putExtra("create", "false");
        } else {
            // Hacemos un insert
            dbWrite.insert(TABLE_CONTACTS, null, cv);
            action.putExtra("create", "true");
        }
        String fullname = (lastName != null && !lastName.equals("")) ? name + " " + lastName : name;
        action.putExtra("fullname", fullname);

        dbWrite.close();
        notifyToTarget(Activity.RESULT_OK);
        Intent intent = getActivity().getIntent();
        intent.putExtra("personID", id);

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(ContactFormFragment.this);
        transaction.commit();


        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getView().getWindowToken(), 0);
        return true;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                ImageView image = (ImageView) getView().findViewById(R.id.edit_image_contact);
                image.setImageBitmap(imageBitmap);
                fotoOk = true;
                return;
            }
        }
    }

    private void notifyToTarget(int code) {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(getTargetRequestCode(), code, null);
        }
    }

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

    public LatLng getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(getView().getContext());
        List<Address> address;
        LatLng p1=null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng ((location.getLatitude() * 1E6), (location.getLongitude() * 1E6));


        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1;
    }


}
