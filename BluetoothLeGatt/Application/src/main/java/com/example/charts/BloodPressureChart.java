package com.example.uichart.ui.deviceinfo;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.uichart.ui.json.JSONBloodPressure;
import com.example.uichart.ui.json.JSONDevice;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class BloodPressureChart extends  MyChart
{
    Context context;
    LineChart chart;
    //LineData data;
    JSONDevice myJSONdevice;
    String myurl;

    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    public BloodPressureChart(Context context, LinearLayout rl, JSONDevice myJSONdevice, String url, RequestQueue queue)
    {
        super(context,rl,myJSONdevice,url, queue);
        this.myurl = url;
        this.context = context;
        this.myJSONdevice = myJSONdevice;
        chart = new LineChart(context);
        rl.addView(chart, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        InitChart();
    }
    List<Entry> line_highest;
    List<Entry> line_lowest;
    LineDataSet dataset_highest;
    LineDataSet dataset_lowest;
    LineData data;
    public void InitChart()
    {
        dataSets = new ArrayList<>();
        line_highest = new ArrayList<Entry>();
        line_lowest = new ArrayList<Entry>();
        Date currentTime = Calendar.getInstance().getTime();
        float currenttime2 = currentTime.getHours()*3600 + currentTime.getMinutes()*60 + currentTime.getSeconds();
        Log.d("TOAN3",currenttime2+"");
        //line_highest.add(new Entry(currenttime2, 120));
        dataset_highest = new LineDataSet(line_highest,"highestPressure");
        dataset_highest.setLineWidth(4);
        dataset_highest.setColor(Color.RED);
        dataSets.add(dataset_highest);

        dataset_lowest = new LineDataSet(line_lowest,"lowestPressure");
        dataset_lowest.setLineWidth(4);
        dataSets.add(dataset_lowest);


        data = new LineData(dataSets);
        chart.getXAxis().setValueFormatter(new LineChartXAxisValueFormatter());
        chart.setData(data);


        chart.notifyDataSetChanged();
        chart.invalidate();
    }
    @Override
    public void GetData(RequestQueue queue)
    {
        //RequestQueue queue = Volley.newRequestQueue(context);
        String url =myurl+ myJSONdevice.id+ "/properties/";
        Log.d("TOAN2","Loading  " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("TOAN2","Response is: "+ response);
                        Gson gson = new Gson();
                        JSONBloodPressure JSONbloodPressure = gson.fromJson(response, JSONBloodPressure.class);
                        Date currentTime = Calendar.getInstance().getTime();
                        float currenttime2 = currentTime.getHours()*3600 + currentTime.getMinutes()*60 + currentTime.getSeconds();
                        Log.d("TOAN31",currenttime2+"");
                        chart.getData().getDataSets().get(0).addEntry(new Entry(currenttime2, JSONbloodPressure.highestPressure));
                        chart.getData().getDataSets().get(1).addEntry(new Entry(currenttime2, JSONbloodPressure.lowestPressure));

                        chart.getData().notifyDataChanged();
                        chart.notifyDataSetChanged();
                        chart.invalidate();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("TOAN2","That didn't work: "  + error.getMessage()); //textView.setText("That didn't work!");
            }
        });
        queue.add(stringRequest);
    }
}

class LineChartXAxisValueFormatter_BloodPressureChart extends IndexAxisValueFormatter
{
    @Override
    public String getFormattedValue(float value)
    {
        int v = (int)value;
        return v/3600+":" + (v/60)%60 + ":" + v%60;
    }
}