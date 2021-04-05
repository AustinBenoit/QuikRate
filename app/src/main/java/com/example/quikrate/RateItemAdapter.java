package com.example.quikrate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Collections;
import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class RateItemAdapter extends
        RecyclerView.Adapter<RateItemAdapter.ViewHolder> implements RatedItemTouchHelperAdapter{

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView beerTextView;
        public TextView breweryTextView;
        public ImageView photoImageView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            beerTextView = (TextView) itemView.findViewById(R.id.beer_item);
            breweryTextView = (TextView) itemView.findViewById(R.id.brewery_item);
            photoImageView = (ImageView) itemView.findViewById(R.id.rated_image_item);
        }
    }

    private List<RatedItem> rateItems_;
    private DBManager dbManager_;


    // Pass in the contact array into the constructor
    public RateItemAdapter(List<RatedItem> items, DBManager dbManager) {
        rateItems_ = items;
        dbManager_ = dbManager;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public RateItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.rateditem_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RateItemAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        RatedItem item = rateItems_.get(position);

        // Set item views based on your views and data model
        TextView textViewBeer = holder.beerTextView;
        textViewBeer.setText(item.GetBeerName());
        TextView textViewBrewery = holder.breweryTextView;
        textViewBrewery.setText(item.GetBreweryName());
        ImageView imageViewPhoto = holder.photoImageView;

        File imgFile = new File(item.getPhotoPath());
        if(imgFile.exists()) {

            Bitmap beerPhoto = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageViewPhoto.setImageBitmap(beerPhoto);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return rateItems_.size();
    }

    // Item touch for swipe to remove and for drag to reorder
    @Override
    public void onItemDismiss(int position) {
        RatedItem rt = rateItems_.get(position);
        dbManager_.delete(rt);
        rateItems_.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(rateItems_, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(rateItems_, i, i - 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
        dbManager_.reorder(fromPosition, toPosition, rateItems_);
        return true;
    }

}