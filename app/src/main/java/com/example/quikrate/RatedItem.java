package com.example.quikrate;

import java.util.ArrayList;

public class RatedItem {
    private String beer_;
    private String brewery_;
    private String photoPath_;

    public RatedItem(String beer, String brewery, String photoPath){

        beer_ = beer;
        brewery_ = brewery;
        photoPath_ = photoPath;
    }

    public String GetBeerName () {
        return beer_;
    }
    public String GetBreweryName() { return brewery_; }
    public void SetPhotoPath(String photoPath){ photoPath_ = photoPath; }
    public String getPhotoPath(){return photoPath_; };

    private static int lastContactId_ = 0;
}
