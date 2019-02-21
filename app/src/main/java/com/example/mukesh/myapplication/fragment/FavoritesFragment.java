package com.example.mukesh.myapplication.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mukesh.myapplication.model.PlaceResult;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.adapter.PlaceResultAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;


public class FavoritesFragment extends Fragment{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    List<PlaceResult> sharedPlaceResultList = new ArrayList<PlaceResult>();
    private View view;

    public FavoritesFragment() {
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
        view = inflater.inflate(R.layout.favorites_fragment, container, false);

        loadFavorites();
        return view;
    }

    private void loadFavorites() {

        TextView t = (TextView) view.findViewById(R.id.textView19);
        recyclerView = (RecyclerView) view.findViewById(R.id.fav_recycler_view);

        t.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String sharedData = sharedpreferences.getString("placeList",null);
        if(sharedData != null) {
            sharedPlaceResultList  = new Gson().fromJson(sharedData, new TypeToken<List<PlaceResult>>(){}.getType());
        }

        if(sharedPlaceResultList.size() > 0) {

            recyclerView.setHasFixedSize(true);
            recyclerView.setVisibility(View.VISIBLE);

            layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);

            mAdapter = new PlaceResultAdapter(sharedPlaceResultList, getActivity(), view);
            recyclerView.setAdapter(mAdapter);
        } else{
            t.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

}