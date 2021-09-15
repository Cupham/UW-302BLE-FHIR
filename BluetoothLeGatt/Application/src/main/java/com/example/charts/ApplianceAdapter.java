package com.example.charts;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.bluetoothlegatt.R;
import com.example.toan.ApplianceManager;
import com.example.toan.ApplianceType;
import com.example.uichart.ui.json.JSONDevice;
import com.example.uichart.ui.json.JSONDevices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ApplianceAdapter extends ArrayAdapter<String> {

    String[] maintitle;
    Context context;
    JSONDevices JSONdevices;
    RequestQueue queue;
    ArrayList<MyChart> charts;
    View[] views;

    public ApplianceAdapter(Context context, String[] maintitle, JSONDevices JSONdevices)
    {
        super(context, R.layout.item_appliance,maintitle);
        this.maintitle = maintitle;
        this.JSONdevices = JSONdevices;
        this.context = context;
        queue = Volley.newRequestQueue(context);
        charts = new ArrayList<>();
        views= new View[maintitle.length];
    }
    public View getView(int position, View view, ViewGroup parent)
    {
        //return CreateView(position,view,parent);
        if(views[position]==null)
            views[position] =  CreateView(position,view,parent);
        return views[position];

    }
    String current_selected_Mode="";
    View CreateView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.item_appliance, parent, false);
        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        titleText.setText(JSONdevices.devices.get(position).id);
        LinearLayout chartview = (LinearLayout)rowView.findViewById(R.id.item_chart_view);
        JSONDevice JSONdevice = JSONdevices.devices.get(position);
        ApplianceType mytype = ApplianceManager.GetApplianceTypeFromTypeString(JSONdevice.deviceType,JSONdevice.installationLocation );

        imageView.setImageResource(ApplianceManager.GetIconIdByType(mytype) );
        String URL = "http://150.65.231.31:5000/elapi/v1/devices/";
        MyChart chart = MyChart.NewChartClass(context, chartview,JSONdevice,URL,queue);

        chart.TextView_Mode = (TextView) rowView.findViewById(R.id.textview_currentmode);
        charts.add(chart);
        final Handler h = new Handler();
        h.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                h.postDelayed(this, 5000);
                for (MyChart chart : charts)
                {
                    chart.GetData(queue);
                }
            }
        }, 50); // 1 second delay (takes millis)




        if(ApplianceManager.IsSupportONOFF((mytype)))
        {
            (rowView.findViewById(R.id.button_on)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendoperationStatusData(JSONdevices.devices.get(position), true);
                }
            });

            (rowView.findViewById(R.id.button_off)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendoperationStatusData(JSONdevices.devices.get(position), false);
                }
            });
        }
        else
        {
            rowView.findViewById(R.id.button_on).setVisibility(View.GONE);
            rowView.findViewById(R.id.button_off).setVisibility(View.GONE);
        }

        if(ApplianceManager.IsSupportMODE(mytype))
        {
            ((Spinner)rowView.findViewById(R.id.spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position2, long id)
                {
                    current_selected_Mode = parent.getItemAtPosition(position2).toString();
                    //SendoperationMode(JSONdevices.devices.get(position),selectedText);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });

            (rowView.findViewById(R.id.button_send)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    SendoperationMode(JSONdevices.devices.get(position),current_selected_Mode);
                }
            });
        }
        else
        {
            rowView.findViewById(R.id.spinner).setVisibility(View.GONE);
            rowView.findViewById(R.id.textview_currentmode).setVisibility(View.INVISIBLE);
            rowView.findViewById(R.id.button_send).setVisibility(View.GONE);

        }
        return rowView;
    }
    void SendoperationMode(JSONDevice myJSONdevice, String b)
    {
        String url =URL+ myJSONdevice.id+ "/properties/operationMode/";
        JSONObject postData = new JSONObject();
        try {
            postData.put("operationMode", b.toLowerCase(Locale.ROOT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, postData, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d("TOAN45","put ok: " + response);
                Toast.makeText(context,"PUT OK: " + response,Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("TOAN45","put error: " + error.getMessage() + " "+ error.hashCode());
                Toast.makeText(context,"PUT FAIL: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }
    String URL =  "http://150.65.231.31:5000/elapi/v1/devices/";
    void SendoperationStatusData(JSONDevice myJSONdevice, boolean b)
    {
        String url =URL+ myJSONdevice.id+ "/properties/operationStatus/";
        JSONObject postData = new JSONObject();
        try {
            postData.put("operationStatus", b);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, postData, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d("TOAN45","put ok: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("TOAN45","put error: " + error.getMessage() + " "+ error.hashCode());
            }
        });
        queue.add(jsonObjectRequest);
    }

}

