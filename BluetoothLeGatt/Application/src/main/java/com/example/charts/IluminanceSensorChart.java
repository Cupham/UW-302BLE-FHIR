package com.example.charts;

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
import com.example.uichart.ui.json.JSONBodyWeighing;
import com.example.uichart.ui.json.JSONDevice;
import com.example.uichart.ui.json.JSONIluminanceSensor;
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

public class IluminanceSensorChart extends MyChart
{
    Context context;
    LineChart chart;
    JSONDevice myJSONdevice;
    String myurl;
    List<Entry> list;

    public IluminanceSensorChart(Context context, LinearLayout rl, JSONDevice myJSONdevice, String url, RequestQueue queue)
    {
        super(context,rl,myJSONdevice,url, queue);
        this.myurl = url;
        this.context = context;
        this.myJSONdevice = myJSONdevice;
        chart = new LineChart(context);
        rl.addView(chart, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        InitChart();
    }
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    List<Entry> line_entry;
    LineDataSet dataset_highest;
    LineDataSet dataset_lowest;
    LineData data;
    public void InitChart()
    {
        dataSets = new ArrayList<>();
        line_entry = new ArrayList<Entry>();
        Date currentTime = Calendar.getInstance().getTime();
        float currenttime2 = currentTime.getHours()*3600 + currentTime.getMinutes()*60 + currentTime.getSeconds();
        Log.d("TOAN3",currenttime2+"");
        dataset_highest = new LineDataSet(line_entry,"Kg");
        dataset_highest.setDrawCircles(false);
        dataset_highest.setLineWidth(4);
        dataset_highest.setColor(Color.MAGENTA);
        dataSets.add(dataset_highest);


        data = new LineData(dataSets);
        chart.getXAxis().setValueFormatter(new LineChartXAxisValueFormatter_IluminanceSensor());

        data.setDrawValues(false);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        //chart.getAxisLeft().setAxisMinimum(-0.1f);
        //chart.getAxisLeft().setAxisMaximum(1.1f);
        chart.getLegend().setEnabled(false);
        chart.setDrawMarkers(false);

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
                        JSONIluminanceSensor json = null;
                        try
                        {
                            json = gson.fromJson(response, JSONIluminanceSensor.class);
                        }
                        catch (Exception e)
                        {
                            Log.d("TOAN123","HERE");
                            return;
                        }

                        if(json.value!=null)
                        {
                            Date currentTime = Calendar.getInstance().getTime();
                            float currenttime2 = currentTime.getHours() * 3600 + currentTime.getMinutes() * 60 + currentTime.getSeconds();
                            Log.d("TOAN31", currenttime2 + ", " + json.toString() + " " + json.value);
                            chart.getData().getDataSets().get(0).addEntry(new Entry(currenttime2, json.value));
                            chart.getData().notifyDataChanged();
                            chart.notifyDataSetChanged();
                            chart.invalidate();
                        }
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

class LineChartXAxisValueFormatter_IluminanceSensor extends IndexAxisValueFormatter
{

    @Override
    public String getFormattedValue(float value)
    {
        int v = (int)value;
        return v/3600+":" + (v/60)%60 + ":" + v%60;
    }

}