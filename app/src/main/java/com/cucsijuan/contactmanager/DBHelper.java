package com.cucsijuan.contactmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ana-pc on 28/02/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper mInstance = null;

    public static final String TABLE_CONTACTS = "contacts";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_LASTNAME = "lastname";
    public static final String COL_EMAIL = "email";
    public static final String COL_PHONE = "phone";
    public static final String COL_PHOTO = "photo";
    public static final String COL_LONG= "loclong";
    public static final String COL_LAT= "loclat";

    private static final String DATABASE_NAME = "Contacts.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_CONTACTS + "( " + COL_ID
            + " integer primary key autoincrement, "
            + COL_NAME + " text not null,"
            + COL_LASTNAME + " text not null,"
            + COL_EMAIL + " text ,"
            + COL_PHONE + " text ,"
            + COL_LONG + " text ,"
            + COL_LAT + " text ,"
            + COL_PHOTO + " text " +
            ");";

    private  DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

}

