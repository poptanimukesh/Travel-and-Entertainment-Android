package com.example.mukesh.myapplication.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mukesh.myapplication.R;

import org.json.JSONObject;


public class InfoFragment extends Fragment{
    private String response, yelpResponse;

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_info, container, false);

        Bundle b = getArguments();
        response = b.getString("key");
        yelpResponse = b.getString("yelpResponse");

        initComponents(view);
        return view;
    }

    private void initComponents(View v) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.has("result")) {
                jsonObject = (JSONObject) jsonObject.get("result");


                if (jsonObject.has("formatted_address")) {
                    ((TextView) v.findViewById(R.id.textView8)).setText(jsonObject.getString("formatted_address"));
                } else{
                    v.findViewById(R.id.tr1).setVisibility(View.GONE);
                }

                if (jsonObject.has("formatted_phone_number")) {
                    TextView textView = ((TextView) v.findViewById(R.id.textView10));
                    textView.setText(Html.fromHtml("<a href=\"tel:" + jsonObject.getString("formatted_phone_number") +
                                                            "\">" + jsonObject.getString("formatted_phone_number") + "</a>"));
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                } else{
                    v.findViewById(R.id.tr2).setVisibility(View.GONE);
                }

                if (jsonObject.has("price_level")) {
                    int level = jsonObject.getInt("price_level");
                    String price_lvl = "";
                    for (int i = 1; i <= level; i++)
                        price_lvl += "$";
                    ((TextView) v.findViewById(R.id.textView12)).setText(price_lvl);
                } else{
                    v.findViewById(R.id.tr3).setVisibility(View.GONE);
                }

                if (jsonObject.has("rating")) {
                    RatingBar ratingBar = ((RatingBar) v.findViewById(R.id.ratingBar));
                    Float rating = Float.parseFloat(jsonObject.get("rating").toString());
                    ratingBar.setRating(rating);
                } else{
                    v.findViewById(R.id.tr4).setVisibility(View.GONE);
                }

                if (jsonObject.has("url")) {
                    TextView textView = ((TextView) v.findViewById(R.id.textView16));
                    textView.setText(Html.fromHtml(
                                    "<a href=\"" + jsonObject.getString("url") +"\">" +
                                            jsonObject.getString("url") + "</a> "));
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                } else{
                    v.findViewById(R.id.tr5).setVisibility(View.GONE);
                }

                if (jsonObject.has("website")) {
                    TextView textView = ((TextView) v.findViewById(R.id.textView18));
                    textView.setText(Html.fromHtml(
                            "<a href=\"" + jsonObject.getString("website") +"\">" +
                                    jsonObject.getString("website") + "</a> "));
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                } else{
                    v.findViewById(R.id.tr6).setVisibility(View.GONE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}