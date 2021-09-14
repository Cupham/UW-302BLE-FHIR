package com.example.charts;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.android.bluetoothlegatt.R;
import com.example.uichart.ui.json.JSONDevice;
import com.example.uichart.ui.json.JSONDevices;

import java.util.ArrayList;

public class ApplicanceAdapter extends ArrayAdapter<String> {

    String[] maintitle;
    Context context;
    JSONDevices JSONdevices;
    RequestQueue queue;
    ArrayList<MyChart> charts;
    View[] views;

    public ApplicanceAdapter(Context context,String[] maintitle, JSONDevices JSONdevices)
    {
        super(context, R.layout.item_applicance,maintitle);
        this.maintitle = maintitle;
        this.JSONdevices = JSONdevices;
        this.context = context;
        queue = Volley.newRequestQueue(context);
        charts = new ArrayList<>();
        views= new View[maintitle.length];
    }
    public View getView(int position, View view, ViewGroup parent)
    {
        if(views[position]==null)
            views[position] =  CreateView(position,view,parent);
        return views[position];

    }
    View CreateView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.item_applicance, null, true);
        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        imageView.setImageResource(R.drawable.tile);
        titleText.setText(JSONdevices.devices.get(position).id);
        LinearLayout chartview = (LinearLayout)rowView.findViewById(R.id.item_chart_view);
        JSONDevice JSONdevice = JSONdevices.devices.get(position);
        String URL = "http://150.65.231.31:5000/elapi/v1/devices/";
        MyChart chart = MyChart.NewChartClass(context, chartview,JSONdevice,URL,queue);
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
        return rowView;
    }
}

