package com.example.quikrate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class AddItemActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int duration = Toast.LENGTH_SHORT;

    String currentPhotoPath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
    }

    // Capture text and return
    public void addButton(View view) {
        Context context = getApplicationContext();
        EditText editText = (EditText) findViewById(R.id.Brewery);
        String brewery = editText.getText().toString().trim();
        editText = (EditText) findViewById(R.id.Beer);
        String beer = editText.getText().toString().trim();
        if (currentPhotoPath == null || currentPhotoPath.isEmpty()) {
            Toast toast = Toast.makeText(context, "Please take a photo", duration);
            toast.show();
            return;
        }

        if (brewery == null || brewery.isEmpty()) {
            Toast toast = Toast.makeText(context, "Please fill in the brewery", duration);
            toast.show();
            return;
        }

        if (beer == null || beer.isEmpty()) {
            Toast toast = Toast.makeText(context, "Please fill in the beer name", duration);
            toast.show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_MESSAGE_BREWERY, brewery);
        intent.putExtra(MainActivity.EXTRA_MESSAGE_BEER, beer);
        intent.putExtra(MainActivity.EXTRA_MESSAGE_PHOTOFILEPATH, currentPhotoPath);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //click to add photo
    public void newRatedItemPhotoButton (View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 0);


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.quikrate.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            File imgFile = new File(currentPhotoPath);
            if(imgFile.exists()) {

                Bitmap beerPhoto = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ImageView imageViewPhoto = (ImageView) findViewById(R.id.add_new_beer_image);
                imageViewPhoto.setImageBitmap(beerPhoto);
            }
        }
    }
}