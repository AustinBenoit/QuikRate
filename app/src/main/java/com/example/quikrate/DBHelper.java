package com.example.quikrate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "RatedItems";

    // Table columns
    public static final String _ID = "_id";
    public static final String BREWERY = "brewery";
    public static final String BEER = "beer";
    public static final String RANK = "rank";
    public static final String PHOTOPATH = "photopath";

    // Database Information
    static final String DB_NAME = "QUIKRATE_RATEDITEMS.DB";

    // database version
    static final int DB_VERSION = 5;

    // Creating table query
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + BREWERY + " TEXT NOT NULL, "
            + BEER + " TEXT NOT NULL,"  + RANK + " REAL," + PHOTOPATH + " TEXT NOT NULL" + ");";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
