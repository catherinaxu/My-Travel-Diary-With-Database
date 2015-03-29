package com.example.catherinaxu.mycityfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.util.Log;

import com.example.catherinaxu.mycityfinder.Loc;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "locationManager";
    private static final String TABLE_LOCATIONS = "locations";
    private static final String KEY_ID = "id";
    private static final String KEY_FEATURE_NAME = "feature_name";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_DESCRIPTION = "description";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //creates a table for the first time
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_FEATURE_NAME + " TEXT," +
                KEY_LATITUDE + " REAL," +
                KEY_LONGITUDE + " REAL," +
                KEY_DESCRIPTION + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //upgrades the table with a newer version
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);

        onCreate(db);
    }

    // allows users to add an entry to the database
    public void addLocation(String feature_name, double lat, double lng, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        //creating values
        ContentValues values = new ContentValues();
        values.put(KEY_FEATURE_NAME, feature_name);
        values.put(KEY_LATITUDE, lat);
        values.put(KEY_LONGITUDE, lng);
        values.put(KEY_DESCRIPTION, description);

        db.insert(TABLE_LOCATIONS, null, values);
        db.close();
    }

    // returns a location object given an id (the entry #)
    public Loc getLocationById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LOCATIONS, new String[] { KEY_ID,
                KEY_FEATURE_NAME, KEY_LATITUDE, KEY_LONGITUDE,
                KEY_DESCRIPTION}, KEY_ID + "=?", new String[] {
                String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Loc location = new Loc(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3),
                cursor.getString(4));

        return location;
    }

    // returns all locations in an ArrayList
    public List<Loc> getAllLocations() {
        List<Loc> locationList = new ArrayList<Loc>();
        String selectQuery = "SELECT * FROM " + TABLE_LOCATIONS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Loc location = new Loc(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3),
                        cursor.getString(4));
                locationList.add(location);
            } while (cursor.moveToNext());
        }
        return locationList;
    }

    // deletes a record based on the ID
    public void deleteLocation(Loc location) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATIONS, KEY_ID + " = ?", new String[] { String.valueOf(location.getId()) });
        db.close();
    }

    //TODO: add update, delete, and get count methods
}
