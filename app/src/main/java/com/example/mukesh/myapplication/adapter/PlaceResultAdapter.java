package com.example.mukesh.myapplication.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mukesh.myapplication.activity.PlaceDetailActivity;
import com.example.mukesh.myapplication.model.PlaceResult;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.activity.SearchResultActivity;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaceResultAdapter extends RecyclerView.Adapter<PlaceResultAdapter.ViewHolder> {
    private List<PlaceResult> placeResultList;
    private Activity searchResultActivity;
    private GeoDataClient geoDataClient;
    private List<PlacePhotoMetadata> photosDataList;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    List<PlaceResult> sharedPlaceResultList = new ArrayList<PlaceResult>();
    private ProgressDialog nDialog;
    private View favoritesView;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView txtFooter;
        public ImageView icon;
        public ImageView favIcon;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
            txtFooter = (TextView) v.findViewById(R.id.secondLine);
            icon = (ImageView) v.findViewById(R.id.authorImage);
            favIcon = (ImageView) v.findViewById(R.id.fav_icon);

            favIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(favIcon.getDrawable().getConstantState() != searchResultActivity.getDrawable(R.drawable.heart_fill_red).getConstantState()) {
                        favIcon.setImageResource(R.drawable.heart_fill_red);

                        sharedPlaceResultList.add(placeResultList.get(getPosition()));
                        String json = new Gson().toJson(sharedPlaceResultList);

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("placeList", json);
                        editor.commit();

                        Toast.makeText(searchResultActivity, placeResultList.get(getPosition()).getName() + " was added to favorites",
                                Toast.LENGTH_SHORT).show();

                    } else{
                        favIcon.setImageResource(R.drawable.heart_outline_black);

                        String placeId = placeResultList.get(getPosition()).getPlaceId();
                        int index=-1;
                        for(int i = 0 ; i<sharedPlaceResultList.size(); i++) {
                            if(sharedPlaceResultList.get(i).getPlaceId().equalsIgnoreCase(placeId)) {
                                Toast.makeText(searchResultActivity, sharedPlaceResultList.get(i).getName() + " was removed to favorites",
                                        Toast.LENGTH_SHORT).show();
                                index = i;
                                break;
                            }
                        }
                        if(index!=-1){
                            sharedPlaceResultList.remove(index);
                        }

                        String json = new Gson().toJson(sharedPlaceResultList);

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("placeList", json);
                        editor.commit();

                        if(!(searchResultActivity instanceof SearchResultActivity)) {
                            placeResultList.remove(getPosition());
                            notifyItemRemoved(getPosition());

                            if(placeResultList.size() == 0) {
                                TextView t = (TextView) favoritesView.findViewById(R.id.textView19);
                                t.setVisibility(View.VISIBLE);

                                favoritesView.findViewById(R.id.fav_recycler_view).setVisibility(View.GONE);
                            }
                        }
                    }


                }
            });
        }

        @Override
        public void onClick(View view) {
            nDialog.setMessage("Fetching results");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
            //Log.d("", "onClick " + getPosition() + " ");
            String placeId = placeResultList.get(getPosition()).getPlaceId();
            fetchResults(placeId);
        }

        public void fetchResults(final String placeId) {
            RequestQueue queue = Volley.newRequestQueue(searchResultActivity);

            String url ="http://devnodejs-env.us-east-1.elasticbeanstalk.com/placedetails?placeid=" + placeId;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            getYelpReviews(response,placeId);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

            queue.add(stringRequest);
        }
    }

    private void getYelpReviews(String response, String placeId){
        try {
            JSONObject jsonObject = new JSONObject(response);
            String name = "", address = "";

            if (jsonObject.has("result")) {
                jsonObject = (JSONObject) jsonObject.get("result");

                if (jsonObject.has("name")) {
                    name = jsonObject.getString("name");
                }

                if (jsonObject.has("formatted_address")) {
                   address = jsonObject.getString("formatted_address");
                }
                fetchYelpReviews(response, placeId, name, address);
            }
        } catch (Exception e) {

        }
    }

    private void fetchYelpReviews(final String response, final String placeId, String name, String address) {
        RequestQueue queue = Volley.newRequestQueue(searchResultActivity);

        String url ="http://devnodejs-env.us-east-1.elasticbeanstalk.com/yelpsearch?name=" + name + "&address=" + address;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String yelpResponse) {
                        launchPlaceDetailActivity(response, placeId, yelpResponse);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                launchPlaceDetailActivity(response, placeId, null);
            }
        });

        queue.add(stringRequest);
    }

    private void launchPlaceDetailActivity(String response, String placeId, String yelpResponse) {
        nDialog.dismiss();
        Intent myIntent = new Intent(searchResultActivity, PlaceDetailActivity.class);
        myIntent.putExtra("key", response); //Optional parameters
        myIntent.putExtra("placeId", placeId);
        myIntent.putExtra("yelpResponse", yelpResponse);
        //myIntent.putExtra("bundle",b);
        searchResultActivity.startActivity(myIntent);
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public PlaceResultAdapter(List<PlaceResult> myDataset, Activity searchResultActivity) {
        initValues(myDataset, searchResultActivity);

    }

    public PlaceResultAdapter(List<PlaceResult> myDataset, Activity searchResultActivity, View favoritesView) {
        this.favoritesView = favoritesView;
        initValues(myDataset, searchResultActivity);
    }

    private void initValues(List<PlaceResult> myDataset, Activity searchResultActivity) {
        this.searchResultActivity = searchResultActivity;
        placeResultList = myDataset;
        geoDataClient = Places.getGeoDataClient(searchResultActivity, null);
        nDialog = new ProgressDialog(searchResultActivity);

        sharedpreferences = searchResultActivity.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String sharedData = sharedpreferences.getString("placeList",null);
        if(sharedData != null) {
            sharedPlaceResultList  = new Gson().fromJson(sharedData, new TypeToken<List<PlaceResult>>(){}.getType());
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PlaceResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());

        View v =
                inflater.inflate(R.layout.row_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final PlaceResult placeResult = placeResultList.get(position);
        holder.txtHeader.setText(placeResult.getName());
        holder.txtFooter.setText(placeResult.getVicinity());
        Picasso.get().load(placeResult.getIcon()).into(holder.icon);

        String sharedData = sharedpreferences.getString("placeList",null);
        if(sharedData != null) {
            sharedPlaceResultList  = new Gson().fromJson(sharedData, new TypeToken<List<PlaceResult>>(){}.getType());
        }

        holder.favIcon.setImageResource(R.drawable.heart_outline_black);
        String placeId = placeResult.getPlaceId();
        int index=0;
        for(index = 0 ; index<sharedPlaceResultList.size(); index++) {
            if(sharedPlaceResultList.get(index).getPlaceId().equalsIgnoreCase(placeId)){
                holder.favIcon.setImageResource(R.drawable.heart_fill_red);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return placeResultList.size();
    }

}