package com.example.mukesh.myapplication.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mukesh.myapplication.model.PlaceResult;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.adapter.PlaceResultAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    int pageNo=0;
    Map<Integer,List<PlaceResult>> pageMap = new HashMap<Integer,List<PlaceResult>>();
    String nextToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Results");

        Intent intent = getIntent();
        String response = intent.getStringExtra("key");

        createList(response);
    }

    public void createList(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);

            if(!jsonObject.getString("status").equalsIgnoreCase("ZERO_RESULTS")) {

                JSONArray results = jsonObject.getJSONArray("results");
                List<PlaceResult> placeResultList = new ArrayList<PlaceResult>();

                for (int i = 0; i < results.length(); i++) {
                    JSONObject c = results.getJSONObject(i);
                    String id = c.getString("place_id");
                    String icon = c.getString("icon");
                    String name = c.getString("name");
                    String vicinity = c.getString("vicinity");

                    placeResultList.add(new PlaceResult(id, icon, name, vicinity));
                }

                if (jsonObject.has("next_page_token") && jsonObject.get("next_page_token") != null) {
                    nextToken = jsonObject.get("next_page_token").toString();
                } else {
                    nextToken = "NA";
                }

                if (nextToken.equalsIgnoreCase("NA") && pageNo == 0) {
                    findViewById(R.id.buttonLayout).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.buttonLayout).setVisibility(View.VISIBLE);
                }

                pageMap.put(pageNo, placeResultList);

                callRecycler(placeResultList);
            } else{
                findViewById(R.id.noResults).setVisibility(View.VISIBLE);
                findViewById(R.id.buttonLayout).setVisibility(View.GONE);
            }
        }catch (Exception e){

        }
    }

    public void  callRecycler(List<PlaceResult> placeResultList) {
        if(pageNo<2 && pageMap.get(pageNo).size()==20) {
            findViewById(R.id.nextBtn).setEnabled(true);
        }else{
            findViewById(R.id.nextBtn).setEnabled(false);
        }

        if(pageNo>0){
            findViewById(R.id.previousBtn).setEnabled(true);
        }else{
            findViewById(R.id.previousBtn).setEnabled(false);
        }

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new PlaceResultAdapter(placeResultList,this);
        recyclerView.setAdapter(mAdapter);
    }

    public void fetchResults(String nextToken) {
        final ProgressDialog nDialog = new ProgressDialog(this);
        nDialog.setMessage("Fetching results");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="http://devnodejs-env.us-east-1.elasticbeanstalk.com/nextsearch?nexttoken=" + nextToken;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        nDialog.dismiss();

                        createList(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(stringRequest);
    }

    public void nextBtnClick(View v) {
        if(pageNo<3) {
            pageNo++;
            if(pageMap.get(pageNo)!=null) {
                callRecycler(pageMap.get(pageNo));
            } else{
                fetchResults(nextToken);
            }
        }
    }

    public void previousBtnClick(View v) {
        if(pageNo>0) {
            pageNo--;
            callRecycler(pageMap.get(pageNo));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }
}
