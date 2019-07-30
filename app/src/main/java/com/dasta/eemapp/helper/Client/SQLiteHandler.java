package com.dasta.eemapp.helper.Client;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Mohamed on 09/08/2017.
 */

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "eem_user";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_MAIL = "user_mail";
    private static final String KEY_USER_PHONE = "user_phone";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_ID + " TEXT,"
                + KEY_USER_NAME + " TEXT," + KEY_USER_MAIL + " TEXT,"
                + KEY_USER_PHONE + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);

    }

    /**
     * Storing user details in database
     */
    public void addUser(String userid, String username, String usermail,
                        String userphone) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userid); //
        values.put(KEY_USER_NAME, username); //
        values.put(KEY_USER_MAIL, usermail); //
        values.put(KEY_USER_PHONE, userphone); //

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    //update mord
    public void updateUser(String userid, String username, String usermail, String userphone) {

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = KEY_USER_ID + " LIKE ?";

        String args[] = {userid};

        ContentValues values = new ContentValues();

        values.put(KEY_USER_NAME, username); //
        values.put(KEY_USER_MAIL, usermail); //
        values.put(KEY_USER_PHONE, userphone); //

        // Inserting Row
        long id = db.update(TABLE_USER, values, selection, args);
        db.close(); // Closing database connection

        Log.d(TAG, "user updated into sqlite: " + id);
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("userid", cursor.getString(1));
            user.put("username", cursor.getString(2));
            user.put("usermail", cursor.getString(3));
            user.put("userphone", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
}
