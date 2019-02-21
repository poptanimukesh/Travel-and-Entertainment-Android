package com.example.mukesh.myapplication.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.model.Reviews;
import com.example.mukesh.myapplication.adapter.ReviewAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class ReviewsFragment extends Fragment{
    private String response;
    private String yelpResponse;
    private ArrayList<Reviews> googleReviewsList =  new ArrayList<Reviews>();
    private ArrayList<Reviews> yelpReviewList = new ArrayList<Reviews>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String []reviews;
    private String []reviewType;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        Bundle b = getArguments();
        response = b.getString("key");
        yelpResponse = b.getString("yelpResponse");

        if(googleReviewsList.isEmpty())
            createGoogleReviewList();

        if(yelpReviewList.isEmpty())
            createYelpReviewsList();

        //callRecycler(view, googleReviewsList);

        Spinner s1 = (Spinner) view.findViewById(R.id.spinner3);
        Spinner s2 = (Spinner) view.findViewById(R.id.spinner4);

        reviews = getResources().getStringArray(R.array.reviews);
        reviewType = getResources().getStringArray(R.array.reviewType);

        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                int index = adapterView.getSelectedItemPosition();
                String item = reviews[index];
                String r = ((Spinner) view.findViewById(R.id.spinner4)).getSelectedItem().toString();
                redrawView(view, item, r);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int i, long l) {
                int index = adapterView.getSelectedItemPosition();
                String item = reviewType[index];
                String r = ((Spinner) view.findViewById(R.id.spinner3)).getSelectedItem().toString();
                redrawView(view, r, item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private void redrawView(View view, String review, String reviewType) {
        ArrayList<Reviews> reviewsList;
        if("Google Reviews".equalsIgnoreCase(review)) {
            reviewsList = googleReviewsList;
        } else{
            reviewsList = yelpReviewList;
        }

        if(reviewsList.size() > 0) {
            view.findViewById(R.id.noReviews).setVisibility(View.GONE);
            view.findViewById(R.id.reviewRecycler).setVisibility(View.VISIBLE);

            ArrayList<Reviews> cloneList = (ArrayList) reviewsList.clone();
            if (reviewType.equalsIgnoreCase("Highest rating")) {
                Collections.sort(cloneList, new RatingComparator());
                Collections.reverse(cloneList);
            } else if (reviewType.equalsIgnoreCase("Lowest rating")) {
                Collections.sort(cloneList, new RatingComparator());
            } else if (reviewType.equalsIgnoreCase("Most recent")) {
                Collections.sort(cloneList, new DateComparator());
                Collections.reverse(cloneList);
            } else if (reviewType.equalsIgnoreCase("Least recent")) {
                Collections.sort(cloneList, new DateComparator());
            }
            callRecycler(view, cloneList);
        } else {
            view.findViewById(R.id.noReviews).setVisibility(View.VISIBLE);
            view.findViewById(R.id.reviewRecycler).setVisibility(View.GONE);
        }
    }

    private void createGoogleReviewList() {
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("result")) {
                JSONArray reviews = ((JSONObject) jsonObject.get("result")).getJSONArray("reviews");

                for (int i = 0; i < reviews.length(); i++) {
                    String photoUrl = "", url = "";
                    JSONObject c = reviews.getJSONObject(i);
                    String name = c.getString("author_name");

                    if(c.has("profile_photo_url"))
                        photoUrl = c.getString("profile_photo_url");

                    float rating = Float.parseFloat(c.getString("rating"));
                    String comment = c.getString("text");
                    Date date = new Date(c.getLong("time") * 1000);

                    if(c.has("author_url"))
                        url = c.getString("author_url");

                    googleReviewsList.add(new Reviews(name, photoUrl, rating, comment, date, url));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createYelpReviewsList() {

        try {
            JSONObject jsonObject = new JSONObject(yelpResponse);

            if (jsonObject.has("reviews")) {
                JSONArray reviews = jsonObject.getJSONArray("reviews");

                for (int i = 0; i < reviews.length(); i++) {
                    JSONObject c = reviews.getJSONObject(i);
                    String name = ((JSONObject)c.get("user")).getString("name");
                    String photoUrl = ((JSONObject)c.get("user")).getString("image_url");
                    float rating = Float.parseFloat(c.getString("rating"));
                    String comment = c.getString("text");
                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(c.getString("time_created"));
                    String url = c.getString("url");

                    yelpReviewList.add(new Reviews(name, photoUrl, rating, comment, date, url));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void callRecycler(View v, List<Reviews> reviewList) {
        recyclerView = (RecyclerView) v.findViewById(R.id.reviewRecycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ReviewAdapter(getActivity(), reviewList);
        recyclerView.setAdapter(mAdapter);
    }

    class RatingComparator implements Comparator<Reviews> {
        public int compare(Reviews a1,Reviews a2){
            if(a1.getRating()==a2.getRating())
                return 0;
            else if(a1.getRating()>a2.getRating())
                return 1;
            else
                return -1;
        }
    }

    public class DateComparator implements Comparator<Reviews>{
        public int compare(Reviews a1,Reviews a2){
            return a1.getDate().compareTo(a2.getDate());
        }
    }
}