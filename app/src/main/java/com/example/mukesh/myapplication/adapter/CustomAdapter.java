package com.example.mukesh.myapplication.adapter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mukesh.myapplication.activity.PlaceDetailActivity;
import com.example.mukesh.myapplication.R;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    private List<PlacePhotoMetadata> photosDataList;
    private PlaceDetailActivity placeDetailActivity;
    private GeoDataClient geoDataClient;

    public CustomAdapter(List<PlacePhotoMetadata> photosDataList, GeoDataClient geoDataClient) {
        this.photosDataList = photosDataList;
        //this.placeDetailActivity = placeDetailActivity;
        this.geoDataClient = geoDataClient;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView photo;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            photo = (ImageView) v.findViewById(R.id.photo_icon);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.image_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            Task<PlacePhotoResponse> photoResponse = geoDataClient.getPhoto(photosDataList.get(position));
            photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                    PlacePhotoResponse photo = task.getResult();
                    Bitmap photoBitmap = photo.getBitmap();
                    viewHolder.photo.setImageBitmap(photoBitmap);
                }
            });
    }

    @Override
    public int getItemCount() {
        return photosDataList.size();
    }
}