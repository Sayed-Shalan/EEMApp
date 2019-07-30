package com.dasta.eemapp.helper.Shop;

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
    private static final String DATABASE_NAME = "eem_shop";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SHOP_ID = "shop_id";
    private static final String KEY_SHOP_NAME = "shop_name";
    private static final String KEY_SHOP_ADDRESS = "shop_address";
    private static final String KEY_SHOP_PHONE = "shop_phone";
    private static final String KEY_OWNER_NAME = "owner_name";
    private static final String KEY_OWNER_MAIL = "owner_mail";
    private static final String KEY_OWNER_USERNAME = "owner_username";
    private static final String KEY_OWNER_CITY = "shop_city";
    private static final String KEY_OWNER_MALL = "shop_mall";
    private static final String KEY_SHOP_IMAGE = "shop_image";
    private static final String KEY_SHOP_LAT = "shop_lat";
    private static final String KEY_SHOP_LNG = "shop_lng";
    private static final String KEY_SHOP_OPEN = "shop_open";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SHOP_ID + " TEXT,"
                + KEY_SHOP_NAME + " TEXT,"
                + KEY_SHOP_ADDRESS + " TEXT," + KEY_SHOP_PHONE + " TEXT,"
                + KEY_OWNER_NAME + " TEXT," + KEY_OWNER_MAIL + " TEXT,"
                + KEY_OWNER_CITY + " TEXT," + KEY_OWNER_MALL + " TEXT,"
                + KEY_OWNER_USERNAME + " TEXT," + KEY_SHOP_IMAGE + " TEXT,"
                + KEY_SHOP_LAT + " TEXT,"
                + KEY_SHOP_LNG + " TEXT," + KEY_SHOP_OPEN + " TEXT" + ")";

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
    public void addUser(String shopid, String shopname,
                        String shopaddress, String shopphone, String ownername, String ownermail, String city, String mall,
                        String ownerusername, String shopimage, String lat, String lng, String open) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SHOP_ID, shopid); //shopid
        values.put(KEY_SHOP_NAME, shopname); //shopname
        values.put(KEY_SHOP_ADDRESS, shopaddress); //shopaddress
        values.put(KEY_SHOP_PHONE, shopphone); //shopphone
        values.put(KEY_OWNER_NAME, ownername); //ownername
        values.put(KEY_OWNER_MAIL, ownermail); //ownermail
        values.put(KEY_OWNER_CITY, city);
        values.put(KEY_OWNER_MALL, mall);
        values.put(KEY_OWNER_USERNAME, ownerusername); //ownerusername
        values.put(KEY_SHOP_IMAGE, shopimage);
        values.put(KEY_SHOP_LAT, lat);
        values.put(KEY_SHOP_LNG, lng);
        values.put(KEY_SHOP_OPEN, open);

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    //update mord
    public void updateUser(String userid, String name, String mail, String username) {

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = KEY_SHOP_ID + " LIKE ?";

        String args[] = {userid};

        ContentValues values = new ContentValues();

        values.put(KEY_OWNER_NAME, name); //
        values.put(KEY_OWNER_MAIL, mail); //
        values.put(KEY_OWNER_USERNAME, username); //

        // Inserting Row
        long id = db.update(TABLE_USER, values, selection, args);
        db.close(); // Closing database connection

        Log.d(TAG, "user updated into sqlite: " + id);
    }

    //update mord
    public void updateShop(String userid, String shopname, String address, String phone, String mall, String city) {

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = KEY_SHOP_ID + " LIKE ?";

        String args[] = {userid};

        ContentValues values = new ContentValues();

        values.put(KEY_SHOP_NAME, shopname); //
        values.put(KEY_SHOP_ADDRESS, address); //
        values.put(KEY_SHOP_PHONE, phone); //
        values.put(KEY_OWNER_MALL, mall); //
        values.put(KEY_OWNER_CITY, city); //

        // Inserting Row
        long id = db.update(TABLE_USER, values, selection, args);
        db.close(); // Closing database connection

        Log.d(TAG, "user updated into sqlite: " + id);
    }

    //update mord
    public void updateLocation(String userid, String lat, String lng) {

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = KEY_SHOP_ID + " LIKE ?";

        String args[] = {userid};

        ContentValues values = new ContentValues();

        values.put(KEY_SHOP_LAT, lat); //
        values.put(KEY_SHOP_LNG, lng); //

        // Inserting Row
        long id = db.update(TABLE_USER, values, selection, args);
        db.close(); // Closing database connection

        Log.d(TAG, "user updated into sqlite: " + id);
    }

    //update mord
    public void updateOpen(String userid, String open) {

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = KEY_SHOP_ID + " LIKE ?";

        String args[] = {userid};

        ContentValues values = new ContentValues();

        values.put(KEY_SHOP_OPEN, open); //

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
            user.put("shopid", cursor.getString(1));
            user.put("shopname", cursor.getString(2));
            user.put("shopaddress", cursor.getString(3));
            user.put("shopphone", cursor.getString(4));
            user.put("ownername", cursor.getString(5));
            user.put("ownermail", cursor.getString(6));
            user.put("ownercity", cursor.getString(7));
            user.put("ownermall", cursor.getString(8));
            user.put("ownerusername", cursor.getString(9));
            user.put("shopimage", cursor.getString(10));
            user.put("shoplat", cursor.getString(11));
            user.put("shoplng", cursor.getString(12));
            user.put("shopopen", cursor.getString(13));
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
