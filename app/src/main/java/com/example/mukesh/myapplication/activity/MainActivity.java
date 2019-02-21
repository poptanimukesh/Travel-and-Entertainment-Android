package com.example.mukesh.myapplication.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mukesh.myapplication.fragment.FavoritesFragment;
import com.example.mukesh.myapplication.R;
import com.example.mukesh.myapplication.fragment.SearchFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.search,
            R.drawable.heart_fill_white
    };
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            location = new MyLocationManager(getApplicationContext()).getLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    location = new MyLocationManager(getApplicationContext()).getLocation();

                }
            }
        }
    }

    public void searchResults(View v) {

        boolean isValid = true;
        String errorText = "Please enter mandatory field";
        if (isEmpty(R.id.editText)) {
            TextView t1 = (TextView)findViewById(R.id.textView5);
            t1.setText(errorText);
            t1.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if(((RadioButton) findViewById(R.id.radioButton2)).isChecked()){
            if (isEmpty(R.id.autoCompleteTextView)) {
                TextView t1 = (TextView)findViewById(R.id.textView6);
                t1.setText(errorText);
                t1.setVisibility(View.VISIBLE);
                isValid = false;
            }
        }

        if(!isValid) {
            Toast.makeText(getApplicationContext(), "Please fix all fields with errors",
                    Toast.LENGTH_SHORT).show();
        } else{
            final ProgressDialog nDialog = new ProgressDialog(this);
            nDialog.setMessage("Fetching results");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String keyword = ((EditText)findViewById(R.id.editText)).getText().toString();
            String category = ((Spinner)findViewById(R.id.spinner)).getSelectedItem().toString().toLowerCase();
            String distance = ((EditText)findViewById(R.id.editText2)).getText().toString();
            String from = (((RadioButton) findViewById(R.id.radioButton)).isChecked())?"here":"location";
            String lat = "" + location.getLatitude();
            String lon = "" + location.getLongitude();//"-118.2584";
            String location = ((AutoCompleteTextView)findViewById(R.id.autoCompleteTextView)).getText().toString();

            String url ="http://devnodejs-env.us-east-1.elasticbeanstalk.com/search?keyword=" + keyword +
                                                    "&category=" + category +
                                                    "&distance=" + distance +
                                                    "&from=" + from +
                                                    "&lat="+ lat +
                                                    "&lon="+ lon +
                                                    "&location=" + location;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            nDialog.dismiss();
                            Intent myIntent = new Intent(MainActivity.this, SearchResultActivity.class);
                            myIntent.putExtra("key", response); //Optional parameters
                            MainActivity.this.startActivity(myIntent);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.getMessage();
                }
            });

            queue.add(stringRequest);
        }

    }

    private boolean isEmpty(int id) {
        String input = ((EditText)findViewById(id)).getText().toString().trim();
        return input.length() == 0;

    }

    public void clearForm(View v) {
        ((TextView)findViewById(R.id.textView5)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.textView6)).setVisibility(View.GONE);

        ((EditText)findViewById(R.id.editText)).setText("");
        ((EditText)findViewById(R.id.editText2)).setText("");
        ((AutoCompleteTextView)findViewById(R.id.autoCompleteTextView)).setText("");
        findViewById(R.id.autoCompleteTextView).setEnabled(false);

        ((RadioButton)findViewById(R.id.radioButton)).setChecked(true);
        ((Spinner)findViewById(R.id.spinner)).setSelection(0);
    }

    public void onRadioButtonClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        switch(v.getId()) {
            case R.id.radioButton:
                if (checked) {
                    ((EditText)findViewById(R.id.autoCompleteTextView)).setEnabled(false);

                    break;
                }
            case R.id.radioButton2:
                if (checked) {
                    ((EditText)findViewById(R.id.autoCompleteTextView)).setEnabled(true);

                    break;
                }
        }
    }

    private void setupTabIcons() {
        LinearLayout tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView tabContent = (TextView) tabLinearLayout.findViewById(R.id.tabContent);
        tabContent.setText("  "+"SEARCH");
        tabContent.setCompoundDrawablesWithIntrinsicBounds(tabIcons[0], 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabContent);

        tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabContent = (TextView) tabLinearLayout.findViewById(R.id.tabContent);
        tabContent.setText("  "+"FAVORITES");
        tabContent.setCompoundDrawablesWithIntrinsicBounds(tabIcons[1], 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabContent);

        LinearLayout linearLayout = (LinearLayout)tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.WHITE);
        drawable.setSize(1, 1);
        linearLayout.setDividerPadding(10);
        linearLayout.setDividerDrawable(drawable);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchFragment(), "SEARCH");
        adapter.addFragment(new FavoritesFragment(), "FAVORITES");
        viewPager.setAdapter(adapter);
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


}
