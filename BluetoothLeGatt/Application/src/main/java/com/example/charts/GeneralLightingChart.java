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
import com.example.uichart.ui.json.JSONDevice;
import com.example.uichart.ui.json.JSONGeneralLighing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GeneralLightingChart extends  MyChart
{
    Context context;
    LineChart chart;
    JSONDevice myJSONdevice;
    String myurl;
    List<Entry> list;

    public GeneralLightingChart(Context context, LinearLayout rl, JSONDevice myJSONdevice, String url, RequestQueue queue)
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
        dataset_highest = new LineDataSet(line_entry,"Light");
        dataset_highest.setLineWidth(4);
        dataset_highest.setColor(Color.GREEN);
        dataSets.add(dataset_highest);


        data = new LineData(dataSets);
        data.setDrawValues(false);

        chart.getXAxis().setValueFormatter(new LineChartXAxisValueFormatter_GeneralLightingChart());
        chart.getAxisLeft().setValueFormatter(new LineChartXAxisValueFormatter_new());
        chart.getAxisRight().setEnabled(false);


        chart.getAxisLeft().setAxisMinimum(-0.1f);
        chart.getAxisLeft().setAxisMaximum(1.1f);
        chart.setData(data);



        //ArrayList<String> yAxisVals = new ArrayList<>(Arrays.asList("OFF", "ON"));

        //chart.getAxisLeft().setValueFormatter(new IndexAxisValueFormatter(yAxisVals));


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
                        JSONGeneralLighing json = gson.fromJson(response, JSONGeneralLighing.class);
                        Date currentTime = Calendar.getInstance().getTime();
                        float currenttime2 = currentTime.getHours()*3600 + currentTime.getMinutes()*60 + currentTime.getSeconds();
                        Log.d("TOAN31",currenttime2+"");
                        chart.getData().getDataSets().get(0).addEntry(new Entry(currenttime2, json.operationStatus==true? 1:0 ));
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

class LineChartXAxisValueFormatter_GeneralLightingChart extends IndexAxisValueFormatter
{

    @Override
    public String getFormattedValue(float value)
    {
        int v = (int)value;
        return v/3600+":" + (v/60)%60 + ":" + v%60;
    }

}


class LineChartXAxisValueFormatter_new extends IndexAxisValueFormatter
{

    @Override
    public String getFormattedValue(float value)
    {
       if(value==0.0) return "OFF";
       if(value==1.0) return "ON";
       return "";
    }

}
