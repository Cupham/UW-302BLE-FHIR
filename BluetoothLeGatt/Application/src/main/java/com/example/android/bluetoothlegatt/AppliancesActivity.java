package com.example.android.bluetoothlegatt;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.charts.ApplianceAdapter;
import com.example.uichart.ui.json.JSONDevices;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

public class AppliancesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicances);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Appliances");
        Log.d("TOAN1","Init applicaces");


        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navmenu);
        navigationView.setItemIconTintList(null);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
        toggle.syncState();
        Log.d("TOAN1","Init applicaces");
        Init();
    }
    void Init()
    {
        ListView lv = findViewById(R.id.list);
       /* lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if ( scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE )
                {
                    lv.invalidateViews();
                }

            }

            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {}
        });*/
        LoadDevices();
    }

    public  static JSONDevices devices = null;
    String LoadDevices()
    {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url ="http://150.65.231.31:5000/elapi/v1/devices";
        Log.d("TOAN1","Loading  " + url);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TOAN1","Response is: "+ response );
                        // Display the first 500 characters of the response string.
                        //textView.setText();
                        Gson gson = new Gson();
                        devices = gson.fromJson(response, JSONDevices.class);
                        Log.d("TOAN1", devices.devices.size() + "");

                        ListView listView=(ListView)findViewById(R.id.list);

                        String[] maintitle = devices.GetIDSStrings();
                        ApplianceAdapter adapter = new ApplianceAdapter(getApplicationContext(),maintitle,devices );
                        listView.setAdapter(adapter);


                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                            {
                                Log.d("TOAN","onItemClick");
                                /*DeviceInfoFragment.myJSONdevice = devices.devices.get(position);
                                ((MainActivity)getActivity()).MyChangeFragment(R.id.nav_gallery);*/
                            }
                        });


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("TOAN1","That didn't work: "  + error.getMessage()); //textView.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

        return "aaa";
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        return MainActivity.I.onNavigationItemSelected(item);
    }
    @Override
    public void onBackPressed()
    {

    }


    @Override
    protected void onResume()
    {
        super.onResume();

    }


}