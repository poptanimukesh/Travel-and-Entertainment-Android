package com.example.mukesh.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mukesh.myapplication.fragment.InfoFragment;
import com.example.mukesh.myapplication.fragment.MapFragment;
import com.example.mukesh.myapplication.fragment.PhotosFragment;
import com.example.mukesh.myapplication.model.PlaceResult;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.fragment.ReviewsFragment;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.info_outline,
            R.drawable.photos,
            R.drawable.maps,
            R.drawable.review
    };
    private String response, yelpResponse;
    private String placeId;
    private GeoDataClient geoDataClient;
    private List<PlacePhotoMetadata> photosDataList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    List<PlaceResult> sharedPlaceResultList = new ArrayList<PlaceResult>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Intent intent = getIntent();
        response = intent.getStringExtra("key");
        placeId = intent.getStringExtra("placeId");
        yelpResponse = intent.getStringExtra("yelpResponse");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getPlaceName());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                | ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                | Gravity.CENTER_VERTICAL);
        layoutParams.rightMargin = 40;

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar_layout, null);
        v.setLayoutParams(layoutParams);
        actionBar.setCustomView(v);

        final ImageView favIcon = v.findViewById(R.id.imageView2);
        final ImageView twitterIcon = v.findViewById(R.id.imageView);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String sharedData = sharedpreferences.getString("placeList",null);
        if(sharedData != null) {
            sharedPlaceResultList  = new Gson().fromJson(sharedData, new TypeToken<List<PlaceResult>>(){}.getType());
        }
        int index=0;
        for(index = 0 ; index<sharedPlaceResultList.size(); index++) {
            if(sharedPlaceResultList.get(index).getPlaceId().equalsIgnoreCase(placeId)) {
                favIcon.setImageResource(R.drawable.heart_fill_white);
            }
        }

        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(favIcon.getDrawable().getConstantState() != getDrawable(R.drawable.heart_fill_white).getConstantState()) {
                    favIcon.setImageResource(R.drawable.heart_fill_white);

                    PlaceResult placeResult = getPlaceResultObject();
                    Toast.makeText(getApplicationContext(), placeResult.getName() + " was added to favorites",
                            Toast.LENGTH_SHORT).show();
                    sharedPlaceResultList.add(placeResult);
                    String json = new Gson().toJson(sharedPlaceResultList);

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("placeList", json);
                    editor.commit();

                } else{
                    favIcon.setImageResource(R.drawable.heart_outline_white);

                    int index=-1;
                    for(int i = 0 ; i<sharedPlaceResultList.size(); i++) {
                        if(sharedPlaceResultList.get(i).getPlaceId().equalsIgnoreCase(placeId)) {
                            Toast.makeText(getApplicationContext(), sharedPlaceResultList.get(i).getName() + " was removed to favorites",
                                    Toast.LENGTH_SHORT).show();
                            index = i;
                            break;
                        }
                    }
                    if(index!=-1) {
                        sharedPlaceResultList.remove(index);
                    }

                    String json = new Gson().toJson(sharedPlaceResultList);

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("placeList", json);
                    editor.commit();
                }
            }
        });

        twitterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String name = "", vicinity = "", website = "";

                    if (jsonObject.has("result")) {
                        jsonObject = (JSONObject) jsonObject.get("result");

                        if (jsonObject.has("name")) {
                            name = jsonObject.getString("name");
                        }

                        if (jsonObject.has("formatted_address")) {
                            vicinity = jsonObject.getString("formatted_address");
                        }

                        if (jsonObject.has("url")) {
                            website = jsonObject.getString("url");
                        }

                        if (jsonObject.has("website")) {
                            website = jsonObject.getString("website");
                        }

                        String url = "https://twitter.com/intent/tweet?text=Check out " + name + " located at " + vicinity + ". Website :  " + website + "#TravelAndEntertainmentSearch";
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        viewPager = (ViewPager) findViewById(R.id.detail_viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.detail_tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle args = new Bundle();
        args.putString("key",response);
        args.putString("placeId",placeId);
        args.putString("yelpResponse", yelpResponse);

        InfoFragment infoFragment = new InfoFragment();
        PhotosFragment photosFragment = new PhotosFragment();
        MapFragment mapFragment = new MapFragment();
        ReviewsFragment reviewsFragment = new ReviewsFragment();

        infoFragment.setArguments(args);
        photosFragment.setArguments(args);
        mapFragment.setArguments(args);
        reviewsFragment.setArguments(args);

        adapter.addFragment(infoFragment, "INFO");
        adapter.addFragment(photosFragment, "PHOTOS");
        adapter.addFragment(mapFragment, "MAP");
        adapter.addFragment(reviewsFragment, "REVIEWS");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        String names[] = {"INFO","PHOTOS","MAP","REVIEWS"};

        for(int i=0;i<4;i++){
            LinearLayout tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tabContent = (TextView) tabLinearLayout.findViewById(R.id.tabContent);
            tabContent.setText("  " + names[i]);
            tabContent.setCompoundDrawablesWithIntrinsicBounds(tabIcons[i], 0, 0, 0);
            tabContent.setPadding(25,0,25,0);
            tabLayout.getTabAt(i).setCustomView(tabContent);
        }

        for(int i=0; i<3; i++) {
            LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
            linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(Color.WHITE);
            drawable.setSize(1, 1);
            linearLayout.setDividerPadding(10);
            linearLayout.setDividerDrawable(drawable);
        }
    }

    private String getPlaceName(){
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.has("result")) {
                jsonObject = (JSONObject) jsonObject.get("result");
                if (jsonObject.has("name")) {
                    return jsonObject.getString("name");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private PlaceResult getPlaceResultObject(){
        try {
            JSONObject jsonObject = new JSONObject(response);
            PlaceResult placeResult = new PlaceResult();

            if (jsonObject.has("result")) {
                jsonObject = (JSONObject) jsonObject.get("result");
                if (jsonObject.has("icon")) {
                    placeResult.setIcon(jsonObject.getString("icon"));
                }

                if (jsonObject.has("place_id")) {
                    placeResult.setPlaceId(jsonObject.getString("place_id"));
                }

                if (jsonObject.has("name")) {
                    placeResult.setName(jsonObject.getString("name"));
                }

                if (jsonObject.has("formatted_address")) {
                    String vicinity = jsonObject.getString("formatted_address");

                    vicinity = vicinity.substring(0, vicinity.lastIndexOf(','));
                    vicinity = vicinity.substring(0, vicinity.lastIndexOf(','));
                    //2527 S San Pedro St, Los Angeles, CA 90011, USA

                    placeResult.setVicinity(vicinity);
                }

                return placeResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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
}
