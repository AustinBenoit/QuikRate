package com.example.quikrate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DBManager dbManager = new DBManager(this);

    ArrayList<RatedItem> ratedItems = new ArrayList<RatedItem>();
    RateItemAdapter adapter;

    public static final String EXTRA_MESSAGE_BREWERY = "com.example.quikrate.BREWERY";
    public static final String EXTRA_MESSAGE_BEER = "com.example.quikrate.BEER";
    public static final String EXTRA_MESSAGE_PHOTOFILEPATH = "com.example.quikrate.PHOTOFILEPATH";

    // Button
    int view = R.layout.activity_main;
    Button button;

    /** Called when the user touches the button */
    public void addNewRatedItem(View view) {
        Intent intent = new Intent(this, AddItemActivity.class);
        int result = 0;
        startActivityForResult(intent, result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Add in checks to see
        //Retrieve data in the intent
        if (resultCode == Activity.RESULT_OK) {
            String beer = data.getStringExtra(EXTRA_MESSAGE_BEER);
            String brewery = data.getStringExtra(EXTRA_MESSAGE_BREWERY);
            String photoFilePath = data.getStringExtra(EXTRA_MESSAGE_PHOTOFILEPATH);
            int len = ratedItems.size();
            RatedItem rt = new RatedItem(beer, brewery, photoFilePath);
            ratedItems.add(rt);
            dbManager.insert(rt);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.text);
        button.setText("+");

        // Lookup the recyclerview in activity layout
        RecyclerView rvItems = (RecyclerView) findViewById(R.id.rvItems);

        //Initalize Contacts
        //TODO Change the adapter to take in a cursor as opposed to the rate items array
        // I may not even need the rated item I can keep everything in the database?
        dbManager.open();
        Cursor cursor = dbManager.fetch();
        while(cursor.moveToNext()) {
            String beer = cursor.getString(cursor.getColumnIndex(DBHelper.BEER));
            String brewery = cursor.getString(cursor.getColumnIndex(DBHelper.BREWERY));
            String photoPath = cursor.getString(cursor.getColumnIndex(DBHelper.PHOTOPATH));
            ratedItems.add(new RatedItem(beer, brewery, photoPath));
        }
        dbManager.close();

        // Create adapter passing in the sample user data
        adapter = new RateItemAdapter(ratedItems, dbManager);
        // Attach the adapter to the recyclerview to populate items
        rvItems.setAdapter(adapter);
        // Set layout manager to position the items
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        // Touch and drag code
        ItemTouchHelper.Callback callback =
                new RatedItemTouchHelperCallBack(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvItems);
    }

}