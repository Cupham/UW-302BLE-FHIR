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
import com.example.charts.MyChart;
import com.example.toan.ApplianceType;
import com.example.uichart.ui.json.JSONBloodPressure;
import com.example.uichart.ui.json.JSONDevice;
import com.example.uichart.ui.json.JSONTemperatureOutside;
import com.example.uichart.ui.json.JSONThermoMeter;
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


public class MergedTemperatureChart extends MyChart
{
    Context context;
    LineChart chart;
    //LineData data;
    JSONDevice myJSONdevice;
    String myurl;

    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    public MergedTemperatureChart(Context context, LinearLayout rl, JSONDevice myJSONdevice, String url, RequestQueue queue)
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
        int[] colors = {Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW, Color.BLACK, Color.CYAN};

        dataSets = new ArrayList<>();
        line_highest = new ArrayList<Entry>();
        line_lowest = new ArrayList<Entry>();
        Date currentTime = Calendar.getInstance().getTime();
        float currenttime2 = currentTime.getHours()*3600 + currentTime.getMinutes()*60 + currentTime.getSeconds();
        Log.d("TOAN3",currenttime2+"");
        //line_highest.add(new Entry(currenttime2, 120));
        for(int i =0; i < myJSONdevice.merged_ids.size(); i++)
        {
            List<Entry> line = new ArrayList<Entry>();
            LineDataSet linedataset = new LineDataSet(line, myJSONdevice.merged_ids.get(i));
            linedataset.setLineWidth(4);
            linedataset.setColor(colors[i]);
            linedataset.setDrawCircles(false);
            dataSets.add(linedataset);
        }


        data = new LineData(dataSets);
        chart.getXAxis().setValueFormatter(new LineChartXAxisValueFormatter());
        data.setDrawValues(false);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setData(data);


        chart.notifyDataSetChanged();
        chart.invalidate();
    }
    @Override
    public void GetData(RequestQueue queue)
    {
        for(int i =0; i < myJSONdevice.merged_ids.size(); i++ )
        {
            ApplianceType appliancetype = myJSONdevice.merged_types.get(i);
            String url =myurl+ myJSONdevice.merged_ids.get(i)+ "/properties/";
            Log.d("TOAN2","Loading  " + url);
            int finalI = i;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {

                            Gson gson = new Gson();
                            Date currentTime = Calendar.getInstance().getTime();
                            float currenttime2 = currentTime.getHours()*3600 + currentTime.getMinutes()*60 + currentTime.getSeconds();
                            if(appliancetype == ApplianceType.CHINICAL_THERMOMETER)
                            {
                                JSONThermoMeter json = gson.fromJson(response, JSONThermoMeter.class);
                                chart.getData().getDataSets().get(finalI).addEntry(new Entry(currenttime2, json.value));
                            }
                            else if(appliancetype == ApplianceType.TEMPERATURE_SENSOR_OUTSIDE)
                            {
                                JSONTemperatureOutside json = gson.fromJson(response, JSONTemperatureOutside.class);
                                chart.getData().getDataSets().get(finalI).addEntry(new Entry(currenttime2, json.value));
                            }


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
}


class LineChartXAxisValueFormatter_MergedTemperatureChart extends IndexAxisValueFormatter
{
    @Override
    public String getFormattedValue(float value)
    {
        int v = (int)value;
        return v/3600+":" + (v/60)%60 + ":" + v%60;
    }
}