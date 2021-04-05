package com.example.quikrate;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.provider.Telephony;

import java.util.ArrayList;
import java.util.List;

public class DBManager {

    private DBHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(RatedItem item) {
        //get the last rank convert to an int and then tack one on.
        // that is the new rank

        // Magic arbitrary starting value that will be able to be divided by two a few times over before becoming a decimal
        double rank = 1024;
        open();

        Cursor cursor = database.query(DBHelper.TABLE_NAME, new String[] { DBHelper.RANK}, null,
                null, null, null, DBHelper.RANK + " Desc");

        //Get the last rank or this is the first one
        if(cursor.moveToNext()){
            int intRank = (int)(cursor.getDouble(cursor.getColumnIndex(dbHelper.RANK)));
            rank = intRank + 1;
        }
        close();

        open();
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.BREWERY, item.GetBreweryName());
        contentValue.put(DBHelper.BEER, item.GetBeerName());
        contentValue.put(DBHelper.RANK, rank );
        contentValue.put(DBHelper.PHOTOPATH, item.getPhotoPath());
        database.insert(DBHelper.TABLE_NAME, null, contentValue);
        close();
    }

    public void delete (RatedItem item){
        open();
        database.delete(DBHelper.TABLE_NAME,  DBHelper.BREWERY + "=?" + " and " + DBHelper.BEER + "=?" ,
                new String[]{item.GetBreweryName(), item.GetBeerName()});
        close();
    }

    public void reorder(int fromRank, int toRank,  List<RatedItem> ratedItems ){
        RatedItem item = ratedItems.get(toRank);
        open();
        double rank  = 0.000001;

        if(toRank == 0){ //first item

            Cursor cursor = database.query(DBHelper.TABLE_NAME, new String[] { DBHelper.RANK}, null,
                    null, null, null, DBHelper.RANK + " Asc");

            double currentFirst;
            if(cursor.moveToNext()){
                currentFirst = cursor.getDouble(cursor.getColumnIndex(dbHelper.RANK));
                rank = (currentFirst / 2.0);
            }

        } else if(toRank == (ratedItems.size() - 1)) { // last item
            Cursor cursor = database.query(DBHelper.TABLE_NAME, new String[] { DBHelper.RANK}, null,
                    null, null, null, DBHelper.RANK + " Desc");

            //Get the last rank or this is the first one
            if(cursor.moveToNext()){
                int intRank = (int)(cursor.getDouble(cursor.getColumnIndex(dbHelper.RANK)));
                rank = intRank + 1;
            }

        } else{ // some where else
            //item before
            RatedItem itemBefore = ratedItems.get(toRank - 1);
            //item after
            RatedItem itemAfter = ratedItems.get(toRank + 1);

            double rankBefore = 0, rankAfter = 0;

            Cursor cursor = database.query(DBHelper.TABLE_NAME, new String[] { DBHelper.RANK},
                    DBHelper.BREWERY + "=?" + " and " + DBHelper.BEER + "=?" ,
                    new String[]{itemBefore.GetBreweryName(), itemBefore.GetBeerName()}, null, null, null);

            //Get the last rank or this is the first one
            if(cursor.moveToNext()){
                rankBefore = cursor.getDouble(cursor.getColumnIndex(dbHelper.RANK));
            }

            cursor = database.query(DBHelper.TABLE_NAME, new String[] { DBHelper.RANK},
                    DBHelper.BREWERY + "=?" + " and " + DBHelper.BEER + "=?" ,
                    new String[]{itemAfter.GetBreweryName(), itemAfter.GetBeerName()}, null, null, null);

            if(cursor.moveToNext()){
                rankAfter = cursor.getDouble(cursor.getColumnIndex(dbHelper.RANK));
            }

            rank = ((rankAfter + rankBefore) /2.0);
        }



        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.BREWERY, item.GetBreweryName());
        contentValue.put(DBHelper.BEER, item.GetBeerName());
        contentValue.put(DBHelper.PHOTOPATH, item.getPhotoPath());
        contentValue.put(DBHelper.RANK, rank );
        database.update(DBHelper.TABLE_NAME, contentValue,DBHelper.BREWERY + "=?" + " and " + DBHelper.BEER + "=?" ,
                new String[]{item.GetBreweryName(), item.GetBeerName()} );
        close();
    }

    public Cursor fetch() {
        String[] columns = new String[] { DBHelper._ID, DBHelper.BREWERY, DBHelper.BEER, DBHelper.RANK, DBHelper.PHOTOPATH};
        //Order by rank
        Cursor cursor = database.query(DBHelper.TABLE_NAME, columns, null, null, null, null, DBHelper.RANK);
        return cursor;
    }
}
