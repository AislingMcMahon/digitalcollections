package com.aisling.digitalcollections;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Donal on 20/03/2016.
 */
public class DigitalCollectionsDbHelper extends SQLiteOpenHelper {
    private final String TAG = DigitalCollectionsDbHelper.class.getSimpleName();

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DigitalCollections.db";
    private static DigitalCollectionsDbHelper mInstance = null;

    private DigitalCollectionsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DigitalCollectionsDbHelper getInstance(Context context)
    {
        if(mInstance == null)
        {
            mInstance = new DigitalCollectionsDbHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DigitalCollectionsContract.SQL_CREATE_BOOKMARKS);
        db.execSQL(DigitalCollectionsContract.SQL_CREATE_USERS);
        db.execSQL(DigitalCollectionsContract.SQL_CREATE_FOLDERS);
        db.execSQL(DigitalCollectionsContract.SQL_CREATE_CONTAINS);
        db.execSQL(DigitalCollectionsContract.SQL_CREATE_QUERIES);
        Log.d(TAG, DigitalCollectionsContract.SQL_CREATE_BOOKMARKS);
        Log.d(TAG, DigitalCollectionsContract.SQL_CREATE_USERS);
        Log.d(TAG, DigitalCollectionsContract.SQL_CREATE_FOLDERS);
        Log.d(TAG, DigitalCollectionsContract.SQL_CREATE_CONTAINS);
        Log.d(TAG, DigitalCollectionsContract.SQL_CREATE_QUERIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DigitalCollectionsContract.SQL_DELETE_BOOKMARKS);
        db.execSQL(DigitalCollectionsContract.SQL_DELETE_USERS);
        db.execSQL(DigitalCollectionsContract.SQL_DELETE_FOLDERS);
        db.execSQL(DigitalCollectionsContract.SQL_DELETE_CONTAINS);
        db.execSQL(DigitalCollectionsContract.SQL_DELETE_QUERIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}