package com.example.mukesh.myapplication.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.example.mukesh.myapplication.activity.MyLocationManager;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.adapter.PlaceAutocompleteAdapter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


public class SearchFragment extends Fragment{
    private  LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(34.02996, -118.28092), new LatLng(35.02996, -117.28092));

    protected GeoDataClient geoDataClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private AutoCompleteTextView mAutocompleteTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myFragmentView = inflater.inflate(R.layout.search_fragment, container, false);
        mAutocompleteTextView = (AutoCompleteTextView) myFragmentView.findViewById(R.id.autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);

        Location location = new MyLocationManager(myFragmentView.getContext()).getLocation();
        if(location != null) {
            BOUNDS = new LatLngBounds(
                    new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(location.getLatitude()+1, location.getLongitude()+1));
        }

        geoDataClient = Places.getGeoDataClient(getActivity(), null);
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(),geoDataClient, BOUNDS,null );
        mAutocompleteTextView.setAdapter(mPlaceAutocompleteAdapter);

        return myFragmentView;
    }

}