package com.example.mukesh.myapplication.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.adapter.PlaceAutocompleteAdapter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    private LatLngBounds BOUNDS;

    protected GeoDataClient geoDataClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private AutoCompleteTextView mAutocompleteTextView;
    private MapView mapView;
    private GoogleMap gmap;
    private String response;
    private double lat,lng;
    LatLng dest;
    String destName;
    com.google.maps.model.LatLng originX;
    String originName;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myFragmentView = inflater.inflate(R.layout.fragment_map, container, false);

        Bundle b = getArguments();
        response = b.getString("key");

        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.has("result")) {
                jsonObject = (JSONObject) jsonObject.get("result");
                destName = jsonObject.getString("name");
                jsonObject = (JSONObject) jsonObject.get("geometry");
                jsonObject = (JSONObject) jsonObject.get("location");
                lat = jsonObject.getDouble("lat");
                lng = jsonObject.getDouble("lng");
                BOUNDS = new LatLngBounds(
                        new LatLng(34.02996, -118.28092), new LatLng(35.02996, -117.28092));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAutocompleteTextView = (AutoCompleteTextView) myFragmentView.findViewById(R.id.autoCompleteTextView2);
        mAutocompleteTextView.setThreshold(3);

        geoDataClient = Places.getGeoDataClient(getActivity(), null);
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), geoDataClient, BOUNDS, null);
        mAutocompleteTextView.setAdapter(mPlaceAutocompleteAdapter);

        mAutocompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                getLatLngFromPlaceName(mAutocompleteTextView.getText().toString());

            }
        });


        mapView = myFragmentView.findViewById(R.id.mapView);
        mapView.onCreate(null);
        mapView.getMapAsync(this);

        Spinner s2 = (Spinner) myFragmentView.findViewById(R.id.spinner2);
        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(originX != null) {
                    int index = adapterView.getSelectedItemPosition();
                    String[] modes = getResources().getStringArray(R.array.drivingModes);
                    String item = modes[index];

                    if (item.equalsIgnoreCase("driving"))
                        getDirections(TravelMode.DRIVING);
                    else if (item.equalsIgnoreCase("bicycling"))
                        getDirections(TravelMode.BICYCLING);
                    else if (item.equalsIgnoreCase("walking"))
                        getDirections(TravelMode.WALKING);
                    else if (item.equalsIgnoreCase("transit"))
                        getDirections(TravelMode.TRANSIT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return myFragmentView;
    }

    private void getDirections(TravelMode travelMode) {
        DateTime now = new DateTime();
        try {
            DirectionsResult results = DirectionsApi.newRequest(getGeoContext())
                    .mode(travelMode).origin(originX)
                    .destination(new com.google.maps.model.LatLng(lat,lng)).departureTime(now)
                    .await();

            gmap.clear();
            gmap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng)).title(originName)).showInfoWindow();
            gmap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng)).title(destName)).showInfoWindow();

            addPolyline(results,gmap);
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey("AIzaSyAA5uUUt95d_FcoR1jIptHe_kg9UUpmHZU")
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(decodedPath);
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(15);
        mMap.addPolyline(polylineOptions);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : decodedPath) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
    }

    private void getLatLngFromPlaceName(final String origin) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = null;
        try {
            url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(origin, "UTF-8") + "&sensor=false&key=AIzaSyBCO5Baz0gwtIHR61pyXUqI9-iYywe_nKs";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("results")) {
                                JSONArray results = jsonObject.getJSONArray("results");
                                jsonObject = (JSONObject) results.getJSONObject(0).get("geometry");
                                jsonObject = (JSONObject) jsonObject.get("location");
                                originX = new com.google.maps.model.LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lng"));
                                originName = origin;
                                getDirections(TravelMode.DRIVING);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(stringRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(15);
        dest = new LatLng(lat, lng);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(dest);
        markerOptions.title(destName);
        gmap.addMarker(markerOptions).showInfoWindow();

        gmap.moveCamera(CameraUpdateFactory.newLatLng(dest));
    }


}