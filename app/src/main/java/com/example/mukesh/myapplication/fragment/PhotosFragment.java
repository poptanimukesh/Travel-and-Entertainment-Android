package com.example.mukesh.myapplication.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.adapter.CustomAdapter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;


public class PhotosFragment extends Fragment{
    private GeoDataClient geoDataClient;
    private List<PlacePhotoMetadata> photosDataList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public PhotosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        geoDataClient = Places.getGeoDataClient(getActivity(), null);

        Bundle b = getArguments();
        getPhotoMetadata(b.getString("placeId"),view);


        // Inflate the layout for this fragment
        return view;
    }

    private void getPhotoMetadata(String placeId, final View view) {

        final Task<PlacePhotoMetadataResponse> photoResponse =
                geoDataClient.getPlacePhotos(placeId);

        photoResponse.addOnCompleteListener
                (new OnCompleteListener<PlacePhotoMetadataResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                        photosDataList = new ArrayList<>();
                        PlacePhotoMetadataResponse photos = task.getResult();
                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();

                        if(photoMetadataBuffer.getCount() > 0) {
                            for (int i = 0; i < photoMetadataBuffer.getCount(); i++) {
                                photosDataList.add(photoMetadataBuffer.get(i).freeze());
                            }

                            photoMetadataBuffer.release();
                            recyclerView = (RecyclerView) view.findViewById(R.id.photoRecycler);
                            recyclerView.setHasFixedSize(true);

                            layoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(layoutManager);

                            mAdapter = new CustomAdapter(photosDataList, geoDataClient);
                            recyclerView.setAdapter(mAdapter);
                        } else{
                            view.findViewById(R.id.noPhotos).setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

}